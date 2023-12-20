package org.example.ewm.event;

import org.example.ewm.categories.CategoryMapper;
import org.example.ewm.event.dto.EventFullDto;
import org.example.ewm.event.dto.EventShortDto;
import org.example.ewm.event.model.Event;
import org.example.ewm.user.UserMapper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventShortDto> toEventsShortDto(List<Event> events) {
        List<EventShortDto> eventShortsDto = new ArrayList<>();
        for (Event event : events) {
            eventShortsDto.add(toEventShortDto(event));
        }
        return eventShortsDto;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getDescription(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.isRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventFullDto> toEventsFullDto(List<Event> events) {
        List<EventFullDto> eventsFullDto = new ArrayList<>();
        for (Event event : events) {
            eventsFullDto.add(toEventFullDto(event));
        }
        return eventsFullDto;
    }

    public static List<EventFullDto> toEventsFullDto(Set<Event> events) {
        List<EventFullDto> eventsFullDto = new ArrayList<>();
        for (Event event : events) {
            eventsFullDto.add(toEventFullDto(event));
        }
        return eventsFullDto;
    }
}
