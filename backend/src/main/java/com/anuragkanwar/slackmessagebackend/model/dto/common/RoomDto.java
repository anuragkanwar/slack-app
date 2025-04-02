package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;
    private String name;
    private String description;
    private RoomType roomType;
    private WorkspaceDto workspace;
    private Set<UserDto> users;
    private UserDto creator;

    public static RoomDto toDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .roomType(room.getRoomType())
                .workspace(WorkspaceDto.toDtoSmall(room.getWorkspace()))
                .users(UserDto.userSetToUserDtoSet(room.getUsers()))
                .creator(UserDto.ToDtoSmall(room.getCreator()))
                .build();
    }


    public static RoomDto toDtoSmall(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .roomType(room.getRoomType())
                .creator(UserDto.ToDtoSmall(room.getCreator()))
                .build();
    }

    public static Set<RoomDto> roomSetToRoomDtoSet(Set<Room> rooms) {
        return rooms.stream().map(RoomDto::toDtoSmall).collect(Collectors.toSet());
    }


}