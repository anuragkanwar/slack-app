package com.anuragkanwar.slackmessagebackend.socket;

import com.anuragkanwar.slackmessagebackend.model.enums.EventType;
import com.anuragkanwar.slackmessagebackend.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {

    @Autowired
    private EventService eventService;

    public void saveEventLog(String message, String room, EventType eventType) {
        log.debug("inside saveInfoMessage");
       /* eventService.save(
                Event.builder()
                        .message(message)
                        .eventType(eventType)
                        .build()
        );*/
    }
}