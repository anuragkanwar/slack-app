package com.anuragkanwar.slackmessagebackend.socket.clientHandlers;


import com.anuragkanwar.slackmessagebackend.model.dto.common.PresenceChangeDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
@Slf4j
public class UserPresenceChangeEventHandler implements ClientEventHandler<PresenceChangeDto> {
    private final SocketRoomManager socketRoomManager;

    public UserPresenceChangeEventHandler(SocketRoomManager socketRoomManager) {
        this.socketRoomManager = socketRoomManager;
    }

    @Override
    public DataListener<PresenceChangeDto> handleEvent() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            String workspaceId = senderClient.getHandshakeData().getHttpHeaders().get("X-workspace-id");
            socketRoomManager.emitToRoom(Utils.getWorkspaceRoom(Utils.decodeFromBase64(workspaceId)), SocketEvent.presence_change, data);
        };
    }

    @Override
    public SocketEvent getIngressEventType() {
        return SocketEvent.manual_presence_change;
    }

    @Override
    public SocketEvent getEgressEventType() {
        return SocketEvent.presence_change;
    }

    @Override
    public Class<PresenceChangeDto> getIngresDataType() {
        return PresenceChangeDto.class;
    }

}
