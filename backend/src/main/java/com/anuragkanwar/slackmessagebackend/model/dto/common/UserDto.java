package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.User;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<WorkspaceDto> workspaces;
    private Set<RoomDto> rooms;


    public static UserDto ToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .workspaces(WorkspaceDto.workspaceSetToWorkspaceDtoSet(user.getWorkspaces()))
                .rooms(RoomDto.roomSetToRoomDtoSet(user.getRooms()))
                .build();
    }

    public static UserDto ToDtoSmall(User user) {

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public static Set<UserDto> userSetToUserDtoSet(Set<User> users) {
        return users.stream().map(UserDto::ToDtoSmall).collect(Collectors.toSet());
    }
}