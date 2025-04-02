package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.MemberLeaveResponseDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberLeftRoomEventHandler implements EventHandler<MemberLeaveResponseDto> {

    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public MemberLeftRoomEventHandler(SocketIOServer socketServer, SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }

    @Override
    public void handleEvent(BaseEvent<MemberLeaveResponseDto> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }

    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.member_left_room;
    }
}
