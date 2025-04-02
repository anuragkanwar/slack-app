package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.response.UserTypingResponseDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserTypingEventHandler implements EventHandler<UserTypingResponseDto> {

    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public UserTypingEventHandler(SocketIOServer socketServer, SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }

    @Override
    public void handleEvent(BaseEvent<UserTypingResponseDto> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }

    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.user_typing;
    }
}
