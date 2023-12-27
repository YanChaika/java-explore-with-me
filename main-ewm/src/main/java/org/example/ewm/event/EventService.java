package org.example.ewm.event;

import org.example.ewm.event.dto.*;
import org.example.explorewithme.dto.ViewStatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventService {

    List<EventShortDto> findEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto findEventByUserAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUserAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> findEventsByParam(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size
            );

    EventFullDto updateEventByEventId(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> findEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size
    );

    EventFullDto findEventById(Long id, ViewStatsDto viewStatsDto);

    CommentFullDto createComment(CommentDto commentDto, Long userId, Long eventId);

    List<CommentFullDto> findCommentsByEventId(Long userId, Long eventId, Integer from, Integer size);
}
