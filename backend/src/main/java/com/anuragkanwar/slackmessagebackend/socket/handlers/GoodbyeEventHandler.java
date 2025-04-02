package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoodbyeEventHandler implements EventHandler<String> {

    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public GoodbyeEventHandler(SocketIOServer socketServer, SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }

    @Override
    public void handleEvent(BaseEvent<String> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }

    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.goodbye;
    }
}
