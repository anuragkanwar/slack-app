package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateUsersInRoomRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface RoomService {
    Room save(CreateRoomRequestDto requestDto);

    boolean existsRoomByName(String name);

    Room getRoomById(Long id);


    Room addUserToRoom(Long userId, Long roomId);

    Room removeUserFromRoom(Long userId, Long roomId);

    Room addUsersToRoom(Long roomId, List<User> users);

    Room deleteRoom(Long roomId, String workspaceId);

    Set<Room> getAllRoomByWorkspaceId(Long workspaceId);

    void updateRoom(UpdateRoomRequestDto requestDto, Long roomId, String worskspaceId);

    @Transactional
    void updateUsersInRoom(Long roomId, UpdateUsersInRoomRequestDto requestDto);
}
