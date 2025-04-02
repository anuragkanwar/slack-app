package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.ChatDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class MessageEvent extends BaseEvent<ChatDto> {
    public MessageEvent(Object source, ChatDto data, String socketRoom) {
        super(source, SocketEvent.message, data, socketRoom);
    }
}
