package com.anuragkanwar.slackmessagebackend.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SocketSessionManager {
    // Key: User ID, Value: Set of Socket.IO session IDs
    private final Map<Long, Set<UUID>> onlineUsers = new ConcurrentHashMap<>();

    public void addSession(Long userId, UUID sessionId) {
        onlineUsers.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        log.info("Added user with ID : {}, to this sessionId : {}", userId, sessionId);
    }

    public Set<UUID> getSessions(Long userId) {
        return onlineUsers.getOrDefault(userId, Collections.emptySet());
    }
}
