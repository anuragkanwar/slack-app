package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.Invite;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.domain.Workspace;
import com.anuragkanwar.slackmessagebackend.model.dto.common.InviteDto;
import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateInviteRequestDto;
import com.anuragkanwar.slackmessagebackend.model.enums.InviteStatus;
import com.anuragkanwar.slackmessagebackend.repository.InviteRepository;
import com.anuragkanwar.slackmessagebackend.repository.UserRepository;
import com.anuragkanwar.slackmessagebackend.repository.WorkspaceRepository;
import com.anuragkanwar.slackmessagebackend.service.InviteService;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.UserJoinedWorkspaceEvent;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InviteServiceImpl implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SocketRoomManager socketRoomManager;

    public InviteServiceImpl(InviteRepository inviteRepository, UserRepository userRepository,
                             WorkspaceRepository workspaceRepository, ApplicationEventPublisher applicationEventPublisher, SocketRoomManager socketRoomManager) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.socketRoomManager = socketRoomManager;
    }

    @Transactional
    @Override
    public List<InviteDto> findInvitesByInviteeAndStatus_Pending() {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        User user = userRepository.getReferenceById(userId);
        return inviteRepository.findInvitesByInvitee(user).stream().map(InviteDto::toDto).toList();
    }


    @Transactional
    @Override
    public void accept(Long inviteId) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        User user = userRepository.getUserById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User does not exists with id: " + userId));

        Invite invite = inviteRepository.getInviteById(inviteId).orElseThrow(() ->
                new ResourceNotFoundException("Invite does not exists with id: " + inviteId)
        );

        invite.setStatus(InviteStatus.ACCEPTED);

        Workspace workspace =
                workspaceRepository.getWorkspaceById(invite.getWorkspace().getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Workspace does not exists with " +
                                        "id:" + invite.getWorkspace().getId()));


        workspace.getUsers().add(user);
        workspaceRepository.save(workspace);

        // send user_joined_workspace and other as well
        applicationEventPublisher.publishEvent(new UserJoinedWorkspaceEvent(
                this,
                UserDto.ToDto(user),
                Utils.getWorkspaceRoom(workspace.getId().toString())
        ));
    }

    @Transactional
    @Override
    public void reject(Long inviteId) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Invite invite = inviteRepository.getInviteById(inviteId).orElseThrow(() ->
                new ResourceNotFoundException("Invite does not exists with id: " + inviteId)
        );

        invite.setStatus(InviteStatus.REJECTED);
    }

    @Transactional
    @Override
    public void save(CreateInviteRequestDto requestDto) {
        Long inviterId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        User inviter = userRepository.getReferenceById(inviterId);

        Workspace workspace =
                workspaceRepository.getWorkspaceById(requestDto.getWorkspaceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Workspace does not exists with " +
                                        "id:" + requestDto.getWorkspaceId()));

        requestDto.getInviteeIds().forEach(inviteeId -> {
            User invitee =
                    userRepository.getUserById(inviteeId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Invitee does not exists with " +
                                            "id:" + inviteeId));
            inviteRepository.save(
                    Invite.builder()
                            .status(InviteStatus.PENDING)
                            .invitee(invitee)
                            .inviter(inviter)
                            .workspace(workspace)
                            .build());
        });
    }


}
