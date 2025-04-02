package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

@Component
public class UserJoinedWorkspaceEventHandler implements EventHandler<UserDto> {
    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public UserJoinedWorkspaceEventHandler(SocketIOServer socketServer, SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }

    @Override
    public void handleEvent(BaseEvent<UserDto> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }

    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.user_joined_workspace;
    }
}
