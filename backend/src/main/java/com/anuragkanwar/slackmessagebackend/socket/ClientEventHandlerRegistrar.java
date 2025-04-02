package com.anuragkanwar.slackmessagebackend.socket;

import com.anuragkanwar.slackmessagebackend.socket.clientHandlers.ClientEventHandler;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ClientEventHandlerRegistrar {

    private final Map<SocketEvent, ClientEventHandler<?>> clientHandlersRegistry = new HashMap<>();
    private final SocketIOServer socketIOServer;       // To register listeners

    public ClientEventHandlerRegistrar(List<ClientEventHandler<?>> clientHandlers, SocketIOServer socketIOServer) {
        clientHandlers.forEach(clientHandler -> {
            clientHandlersRegistry.put(clientHandler.getIngressEventType(), clientHandler);
        });
        this.socketIOServer = socketIOServer;
    }

    @PostConstruct
    public void registerClientEventHandlers() {
        log.info("Discovering and registering ClientEventHandler beans as Socket.IO listeners...");

        if (clientHandlersRegistry.isEmpty()) {
            log.warn("No beans implementing ClientEventHandler found. No client event listeners will be registered.");
            return;
        }

        int count = 0;
        for (Map.Entry<SocketEvent, ClientEventHandler<?>> entry : clientHandlersRegistry.entrySet()) {
            String beanName = entry.getKey().name();
            ClientEventHandler<?> handler = entry.getValue(); // Use wildcard initially

            SocketEvent eventType = handler.getIngressEventType();
            Class<?> payloadClass = handler.getIngresDataType();

            if (eventType == null || payloadClass == null) {
                log.error("ClientEventHandler bean '{}' [Class: {}] returned null or empty eventType/payloadClass. Skipping registration.",
                        beanName, handler.getClass().getName());
                continue;
            }

            try {
                // The cast to (DataListener) is necessary because of generics, but safe here.
                socketIOServer.addEventListener(eventType.name(), payloadClass, (DataListener) handler.handleEvent());

                log.info("Registered Socket.IO listener: Event='{}', Payload={}, HandlerBean='{}'",
                        eventType, payloadClass.getSimpleName(), beanName);
                count++;
            } catch (
                    Exception e) {
                log.error("Failed to register Socket.IO listener for event '{}' [HandlerBean: {}]: {}",
                        eventType, beanName, e.getMessage(), e);
            }
        }
        log.info("Finished registering {} Socket.IO client event listeners.", count);
    }

}
