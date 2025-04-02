package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class GoodbyeEvent extends BaseEvent<String> {
    public GoodbyeEvent(Object source, String data, String socketRoom) {
        super(source, SocketEvent.goodbye, data, socketRoom);
    }
}
