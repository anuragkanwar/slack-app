package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BootUserListEventHandler implements EventHandler<List<UserDto>> {

    private final SocketIOServer socketServer;
    private final SocketRoomManager roomManager;

    public BootUserListEventHandler(SocketIOServer socketServer,
                                    SocketRoomManager roomManager) {
        this.socketServer = socketServer;
        this.roomManager = roomManager;
    }


    @Override
    public void handleEvent(BaseEvent<List<UserDto>> event) {
        roomManager.emitToRoom(event.getSocketRoom(), event.getEventType(), event.getData());
    }


    @Override
    public SocketEvent getSupportedEventType() {
        return SocketEvent.boot_user_list;
    }
}
