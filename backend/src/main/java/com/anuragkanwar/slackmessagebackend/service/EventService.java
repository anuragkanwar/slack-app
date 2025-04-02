package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.domain.Event;

public interface EventService {
    Event save(Event event);
}
