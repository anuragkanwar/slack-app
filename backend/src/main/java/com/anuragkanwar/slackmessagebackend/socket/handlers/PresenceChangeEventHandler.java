package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.common.PresenceChangeDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PresenceChangeEventHandler implements EventHandler<PresenceChangeDto> {

    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public PresenceChangeEventHandler(SocketIOServer socketServer, SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }

    @Override
    public void handleEvent(BaseEvent<PresenceChangeDto> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }

    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.presence_change;
    }
}
