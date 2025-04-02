package com.anuragkanwar.slackmessagebackend.socket.handlers;

import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;

// Role: Processes specific event types
public interface EventHandler<T> {
    void handleEvent(BaseEvent<T> event);

    SocketEvent getSupportedEventType();
}
