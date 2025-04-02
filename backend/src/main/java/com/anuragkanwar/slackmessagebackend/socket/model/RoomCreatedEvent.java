package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class RoomCreatedEvent extends BaseEvent<RoomDto> {
    public RoomCreatedEvent(Object source, RoomDto data, String socketRoom) {
        super(source, SocketEvent.room_created, data, socketRoom);
    }
}
