package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.domain.Workspace;
import com.anuragkanwar.slackmessagebackend.model.dto.common.WorkspaceDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateWorkspaceRequestDto;
import com.anuragkanwar.slackmessagebackend.repository.UserRepository;
import com.anuragkanwar.slackmessagebackend.repository.WorkspaceRepository;
import com.anuragkanwar.slackmessagebackend.service.WorkspaceService;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository,
                                UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }


    public WorkspaceDto save(CreateWorkspaceRequestDto workspace) {

        Long creatorId =
                Utils.getCurrentUserIdFromAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());

        if (log.isDebugEnabled()) {
            log.debug("User creatorId {}", creatorId);
        }

        User creator = userRepository.getReferenceById(creatorId);
        Set<User> users = new HashSet<>(List.of(creator));
        var saved_workspace = workspaceRepository.save(
                Workspace.builder()
                        .name(workspace.getName())
                        .creator(creator)
                        .users(users)
                        .build());
        if (log.isDebugEnabled()) {
            log.debug("Workspace rooms, {}", saved_workspace.getRooms());
        }

        return WorkspaceDto.toDto(saved_workspace);
    }

    @Override
    public Set<WorkspaceDto> getWorkspacesByUserId() {

        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        return WorkspaceDto.workspaceSetToWorkspaceDtoSet(
                workspaceRepository.findByUsers_Id(userId));
    }


    @Override
    public WorkspaceDto getWorkspacesById(Long workspaceId) {
        return WorkspaceDto.toDto(workspaceRepository.getWorkspaceById(workspaceId).orElseThrow(
                () -> new ResourceNotFoundException("Workspace not found with id" + workspaceId)
        ));
    }
}
