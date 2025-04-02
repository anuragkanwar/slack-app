package com.anuragkanwar.slackmessagebackend.socket.model;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;

import java.util.List;

public class BootUserListEvent extends BaseEvent<List<UserDto>> {
    public BootUserListEvent(Object source, List<UserDto> data, String socketRoom) {
        super(source, SocketEvent.boot_user_list, data, socketRoom);
    }
}
