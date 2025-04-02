package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.exception.general.BadRequestException;
import com.anuragkanwar.slackmessagebackend.exception.general.ForbiddenException;
import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.domain.Workspace;
import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateUsersInRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.MemberJoinedResponseDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.MemberLeaveResponseDto;
import com.anuragkanwar.slackmessagebackend.model.enums.RoomType;
import com.anuragkanwar.slackmessagebackend.repository.RoomRepository;
import com.anuragkanwar.slackmessagebackend.repository.UserRepository;
import com.anuragkanwar.slackmessagebackend.repository.WorkspaceRepository;
import com.anuragkanwar.slackmessagebackend.service.RoomService;
import com.anuragkanwar.slackmessagebackend.service.UserService;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.SocketSessionManager;
import com.anuragkanwar.slackmessagebackend.socket.model.*;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SocketRoomManager socketRoomManager;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserService userService;
    private final SocketSessionManager socketSessionManager;

    public RoomServiceImpl(RoomRepository roomRepository,
                           ApplicationEventPublisher applicationEventPublisher,
                           SocketRoomManager socketRoomManager,
                           UserRepository userRepository,
                           WorkspaceRepository workspaceRepository,
                           UserService userService,
                           SocketSessionManager socketSessionManager) {
        this.roomRepository = roomRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.socketRoomManager = socketRoomManager;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.userService = userService;
        this.socketSessionManager = socketSessionManager;
    }

    @Transactional
    public Room save(CreateRoomRequestDto requestDto) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Workspace workspace = workspaceRepository.getReferenceById(requestDto.getWorkspaceId());
        User user = userRepository.getReferenceById(userId);
        Set<User> users = new HashSet<>(List.of(user));

        for (Long id : requestDto.getUsers()) {
            users.add(userRepository.getUserById(id).orElseThrow(() ->
                    new ResourceNotFoundException("User does not exists with id: " + id)
            ));
        }
        Room room = roomRepository.save(
                Room.builder()
                        .roomType(requestDto.getRoomType())
                        .description(requestDto.getDescription())
                        .creator(user)
                        .name(requestDto.getName())
                        .users(users)
                        .workspace(workspace)
                        .build()
        );


        // add creator to user list
        requestDto.getUsers().add(userId);

        //sending event
        String socketRoom = Utils.getChannelRoom(room.getId().toString());
        requestDto.getUsers().forEach(memberId -> {
            Set<UUID> sessions = socketSessionManager.getSessions(memberId);
            sessions.forEach(session -> {
                socketRoomManager.joinRoom(session, socketRoom);
            });
        });

        applicationEventPublisher.publishEvent(new RoomCreatedEvent(this,
                RoomDto.toDto(room),
                socketRoom
        ));

        return room;
    }

    @Override
    public boolean existsRoomByName(String name) {
        return roomRepository.existsRoomByName(name);
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.getRoomById(id).orElseThrow(() ->
                new ResourceNotFoundException("Room does not exists with id: " + id)
        );
    }


    @Override
    public Room addUserToRoom(Long userId, Long roomId) {
        User user = userService.getUserById(userId);
        Room room = getRoomById(roomId);
        room.getUsers().add(user);
        return roomRepository.save(room);
    }

    @Override
    public Room removeUserFromRoom(Long userId, Long roomId) {
        User user = userService.getUserById(userId);
        Room room = getRoomById(roomId);
        room.getUsers().remove(user);
        return roomRepository.save(room);
    }

    @Override
    public Room addUsersToRoom(Long roomId, List<User> users) {
        List<User> user = new ArrayList<>();
        for (User user1 : users) {
            user.add(userService.getUserById(user1.getId()));
        }
        Room room = getRoomById(roomId);
        room.getUsers().addAll(user);
        return roomRepository.save(room);
    }

    @Override
    public Room deleteRoom(Long roomId, String workspaceId) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Room room = roomRepository.getRoomById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room does not exists with id: " + roomId));

        if (!room.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete the room");
        }

        Room deletedRoom = roomRepository.deleteRoomById(roomId);

        applicationEventPublisher.publishEvent(new RoomDeletedEvent(
                this,
                RoomDto.toDtoSmall(deletedRoom),
                Utils.getWorkspaceRoom(Utils.decodeFromBase64(workspaceId))
        ));

        return room;

    }

    @Override
    public Set<Room> getAllRoomByWorkspaceId(Long workspaceId) {
        return roomRepository.findByWorkspace_Id(workspaceId);
    }


    @Override
    public void updateRoom(UpdateRoomRequestDto requestDto, Long roomId, String workspaceId) {

        Room existingRoom = roomRepository.getRoomById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room does not exists with id: " + roomId)
        );

        if (existingRoom.getRoomType() == RoomType.DM || existingRoom.getRoomType() == RoomType.MUDM) {
            throw new BadRequestException("Cant Update Direct Message Rooms");
        }


        if (requestDto.getName() != null) {
            existingRoom.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            existingRoom.setDescription(requestDto.getDescription());
        }

        Room savedRoom = roomRepository.save(existingRoom);
        applicationEventPublisher.publishEvent(new RoomRenameEvent(
                this,
                RoomDto.toDto(savedRoom),
                existingRoom.getRoomType() == RoomType.Private ?
                        Utils.getChannelRoom(roomId.toString()) :
                        Utils.getWorkspaceRoom(Utils.decodeFromBase64(workspaceId))));
    }


    @Transactional
    @Override
    public void updateUsersInRoom(Long roomId, UpdateUsersInRoomRequestDto requestDto) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Room room = roomRepository.getRoomById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room does not exists with id: " + roomId));

        if (!room.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete the room");
        }

        User creator = userRepository.getReferenceById(userId);

        Set<Long> existingUsers = room.getUsers().stream().map(User::getId).collect(Collectors.toSet());

        Set<User> users = new HashSet<>(List.of(creator));

        requestDto.getUserIds().forEach(newUserId -> {
            User user = userRepository.getUserById(userId).orElseThrow(() ->
                    new ResourceNotFoundException("User does not exists with id: " + newUserId));

            users.add(user);
        });


        room.setUsers(users);
        roomRepository.save(room);

        // publish event

        // different groups
        Set<Long> A_diff_B = new HashSet<>(existingUsers);
        A_diff_B.removeAll(requestDto.getUserIds());

        Set<Long> B_diff_A = new HashSet<>(requestDto.getUserIds());
        B_diff_A.removeAll(existingUsers);

        // remove existing users from room
        A_diff_B.forEach(id -> {
            socketSessionManager.getSessions(id).forEach(sessionId -> {
                socketRoomManager.leftRoom(sessionId, Utils.getChannelRoom(roomId.toString()));
            });

            applicationEventPublisher.publishEvent(new RoomLeftEvent(
                    this,
                    RoomDto.toDto(room),
                    Utils.getUserRoom(id.toString())
            ));


            applicationEventPublisher.publishEvent(new MemberLeftRoomEvent(
                    this,
                    MemberLeaveResponseDto.builder().roomId(roomId).userId(id).build(),
                    Utils.getChannelRoom(roomId.toString())
            ));
        });


        B_diff_A.forEach(id -> {
            socketSessionManager.getSessions(id).forEach(sessionId -> {
                socketRoomManager.joinRoom(sessionId, Utils.getChannelRoom(roomId.toString()));
            });

            applicationEventPublisher.publishEvent(new RoomJoinedEvent(
                    this,
                    RoomDto.toDto(room),
                    Utils.getUserRoom(id.toString())
            ));

            applicationEventPublisher.publishEvent(new MemberJoinedRoomEvent(
                    this,
                    MemberJoinedResponseDto.builder().roomId(roomId).userId(id).build(),
                    Utils.getChannelRoom(roomId.toString())
            ));
        });
    }
}




































