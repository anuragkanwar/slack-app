package com.anuragkanwar.slackmessagebackend.socket;

import com.anuragkanwar.slackmessagebackend.socket.handlers.EventHandler;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

//Role: Spring's entry point - catches all events and delegates to registry
@Slf4j
@Component
public class GenericEventListener {

    private final EventHandlerRegistry registry;

    public GenericEventListener(EventHandlerRegistry registry) {
        this.registry = registry;
    }

    @Async
    @EventListener
    public void handleBaseEvent(BaseEvent event) {
        log.info("got event {}", event.getEventType().name());
        EventHandler<?> handler = registry.getHandler(event.getEventType());
        if (handler != null) {
            handleEventWithType(event, handler);
        } else {
            log.info("No Handler Found for event , {}", event.getEventType().name());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEvent> void handleEventWithType(T event, EventHandler<?> handler) {
        handler.handleEvent(event);
    }
}
