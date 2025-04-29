package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.Chat;
import com.anuragkanwar.slackmessagebackend.model.enums.ChatType;
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
public class ChatDto {

    private Long id;
    private String message;
    private ChatType chatType;
    private ChatDto parent;
    private UserDto user;
    private RoomDto room;


    public static ChatDto toDto(Chat chat) {
        if (chat == null)
            return null;
        return ChatDto.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .chatType(chat.getChatType())
                .parent(ChatDto.toDtoSmall(chat.getParent()))
                .user(UserDto.ToDtoSmall(chat.getUser()))
                .room(RoomDto.toDtoSmall(chat.getRoom()))
                .build();
    }

    public static ChatDto toDtoSmall(Chat chat) {
        if (chat == null)
            return null;
        return ChatDto.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .chatType(chat.getChatType())
                .room(RoomDto.toDtoSmall(chat.getRoom()))
                .build();
    }

    public static Set<ChatDto> chatSetToChatDtoSet(Set<Chat> chats) {
        return chats.stream().map(ChatDto::toDtoSmall).collect(Collectors.toSet());
    }

}