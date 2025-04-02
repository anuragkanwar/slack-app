package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.PresenceChangeDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class PresenceChangeEvent extends BaseEvent<PresenceChangeDto> {
    public PresenceChangeEvent(Object source, PresenceChangeDto data, String socketRoom) {
        super(source, SocketEvent.presence_change, data, socketRoom);
    }
}
