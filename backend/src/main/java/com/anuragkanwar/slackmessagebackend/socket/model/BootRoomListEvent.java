package com.anuragkanwar.slackmessagebackend.socket.model;

import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;

import java.util.List;

public class BootRoomListEvent extends BaseEvent<List<RoomDto>> {
    public BootRoomListEvent(Object source, List<RoomDto> data, String socketRoom) {
        super(source, SocketEvent.boot_room_list, data, socketRoom);
    }
}
