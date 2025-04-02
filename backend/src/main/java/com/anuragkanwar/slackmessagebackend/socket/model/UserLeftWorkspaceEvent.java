package com.anuragkanwar.slackmessagebackend.socket.model;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;

public class UserLeftWorkspaceEvent extends BaseEvent<UserDto> {
    public UserLeftWorkspaceEvent(Object source, UserDto data, String socketRoom) {
        super(source, SocketEvent.user_left_workspace, data, socketRoom);
    }
}
