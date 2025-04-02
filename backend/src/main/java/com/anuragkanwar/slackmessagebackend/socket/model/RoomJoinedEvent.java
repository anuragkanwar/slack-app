package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class RoomJoinedEvent extends BaseEvent<RoomDto> {
    public RoomJoinedEvent(Object source, RoomDto data, String socketRoom) {
        super(source, SocketEvent.room_joined, data, socketRoom);
    }
}
