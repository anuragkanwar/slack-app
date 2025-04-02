package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.response.UserTypingResponseDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class UserTypingEvent extends BaseEvent<UserTypingResponseDto> {
    public UserTypingEvent(Object source, UserTypingResponseDto data, String socketRoom) {
        super(source, SocketEvent.user_typing, data, socketRoom);
    }
}
