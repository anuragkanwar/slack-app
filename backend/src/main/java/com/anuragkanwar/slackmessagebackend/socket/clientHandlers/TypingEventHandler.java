package com.anuragkanwar.slackmessagebackend.socket.clientHandlers;


import com.anuragkanwar.slackmessagebackend.model.dto.common.PresenceChangeDto;
import com.anuragkanwar.slackmessagebackend.model.dto.common.TypingDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.SocketRoomManager;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TypingEventHandler implements ClientEventHandler<TypingDto> {
    private final SocketRoomManager socketRoomManager;

    public TypingEventHandler(SocketRoomManager socketRoomManager) {
        this.socketRoomManager = socketRoomManager;
    }

    @Override
    public DataListener<TypingDto> handleEvent() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketRoomManager.emitToRoom(
                    Utils.getChannelRoom(data.getRoomId().toString()),
                    SocketEvent.user_typing,
                    data);
        };
    }

    @Override
    public SocketEvent getIngressEventType() {
        return SocketEvent.typing;
    }

    @Override
    public SocketEvent getEgressEventType() {
        return SocketEvent.user_typing;
    }

    @Override
    public Class<TypingDto> getIngresDataType() {
        return TypingDto.class;
    }

}
