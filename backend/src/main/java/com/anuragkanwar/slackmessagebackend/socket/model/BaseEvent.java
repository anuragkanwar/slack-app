package com.anuragkanwar.slackmessagebackend.socket.model;

import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


//Role: Carries event data (source, event type, payload)
@Getter
@Setter
public abstract class BaseEvent<T> extends ApplicationEvent {
    private final SocketEvent eventType;
    private final T data;
    private final String socketRoom;

    public BaseEvent(Object source, SocketEvent eventType, T data, String socketRoom) {
        super(source);
        this.eventType = eventType;
        this.data = data;
        this.socketRoom = socketRoom;
    }
}