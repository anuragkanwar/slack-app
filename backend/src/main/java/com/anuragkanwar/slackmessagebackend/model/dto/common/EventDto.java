package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.Event;
import com.anuragkanwar.slackmessagebackend.model.enums.EventType;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;
    private String message;
    private UserDto user;
    private EventType eventType;


    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .message(event.getMessage())
                .eventType(event.getEventType())
                .user(UserDto.ToDtoSmall(event.getUser()))
                .build();
    }


    public static EventDto toDtoSmall(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .message(event.getMessage())
                .eventType(event.getEventType())
                .build();
    }


    public static Set<EventDto> eventSetToEventDtoSet(Set<Event> events) {
        return events.stream().map(EventDto::toDtoSmall).collect(Collectors.toSet());
    }


}