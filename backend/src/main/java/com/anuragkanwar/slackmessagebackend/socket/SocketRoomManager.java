package com.anuragkanwar.slackmessagebackend.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class SocketRoomManager {

    private final SocketIOServer socketIOServer;

    public SocketRoomManager(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public void joinRoom(UUID sessionId, String room) {
        SocketIOClient client = socketIOServer.getClient(sessionId);

        if (client != null) {
            client.joinRoom(room);
            if (log.isDebugEnabled()) {
                log.info("Joined room : {}, with this sessionId : {}", room, sessionId);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Client does not exists with sessionId :{}", sessionId);
            }
        }
    }

    public void leftRoom(UUID sessionId, String room) {
        SocketIOClient client = socketIOServer.getClient(sessionId);
        if (client != null) {
            client.leaveRoom(room);
            if (log.isDebugEnabled()) {
                log.info("left room : {}, with this sessionId : {}", room, sessionId);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Client does not exists with sessionId :{}", sessionId);
            }
        }
    }

    public void emitToRoom(String room, SocketEvent eventType, Object data) {
        if (log.isDebugEnabled()) {
            log.debug("Inside RoomManger.emitToRoom, GOT event: {}, data: {}, room: {}", eventType, data, room);
        }
        socketIOServer.getRoomOperations(room).sendEvent(eventType.name(), data);
    }
}