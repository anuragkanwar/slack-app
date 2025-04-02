package com.anuragkanwar.slackmessagebackend.socket.model;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;

public class UserJoinedWorkspaceEvent extends BaseEvent<UserDto> {
    public UserJoinedWorkspaceEvent(Object source, UserDto data, String socketRoom) {
        super(source, SocketEvent.user_joined_workspace, data, socketRoom);
    }
}
