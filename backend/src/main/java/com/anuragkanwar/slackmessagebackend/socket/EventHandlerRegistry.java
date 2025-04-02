package com.anuragkanwar.slackmessagebackend.socket;


import com.anuragkanwar.slackmessagebackend.socket.handlers.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Maps event classes → handlers
@Component
public class EventHandlerRegistry {
    private final Map<SocketEvent, EventHandler<?>> handlers = new HashMap<>();

    // Inject all EventHandler implementations into the registry
    @Autowired
    public EventHandlerRegistry(List<EventHandler<?>> handlers) {
        handlers.forEach(handler ->
                this.handlers.put(handler.getSupportedEventType(), handler)
        );
    }

    public EventHandler<?> getHandler(SocketEvent eventType) {
        return handlers.get(eventType);
    }
}