package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.dto.common.WorkspaceDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateWorkspaceRequestDto;

import java.util.Set;


public interface WorkspaceService {
    WorkspaceDto save(CreateWorkspaceRequestDto workspace);

    Set<WorkspaceDto> getWorkspacesByUserId();


    WorkspaceDto getWorkspacesById(Long workspaceId);
}