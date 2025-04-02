package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.Workspace;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDto {

    private Long id;
    private String name;
    private Set<UserDto> users;
    private UserDto creator;
    private Set<RoomDto> rooms;


    public static WorkspaceDto toDto(Workspace workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .creator(UserDto.ToDtoSmall(workspace.getCreator()))
                .users(UserDto.userSetToUserDtoSet(workspace.getUsers()))
                .rooms(RoomDto.roomSetToRoomDtoSet(workspace.getRooms()))
                .build();
    }

    public static WorkspaceDto toDtoSmall(Workspace workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .build();

    }

    public static Set<WorkspaceDto> workspaceSetToWorkspaceDtoSet(Set<Workspace> workspaces) {
        if (workspaces == null) {
            return null;
        }

        return workspaces.stream().map(WorkspaceDto::toDtoSmall).collect(Collectors.toSet());
    }


}