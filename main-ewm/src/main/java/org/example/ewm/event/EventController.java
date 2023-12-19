package org.example.ewm.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.client.stats.StatsClient;
import org.example.ewm.event.dto.*;
import org.example.ewm.exception.ValidationException;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> findEventsByUserId(@Positive @PathVariable(name = "userId") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                        Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10")
                                                      Integer size) {
        log.info("Get events by userId={}, from={}, size={}", userId, from, size);
        return eventService.findEventsByUserId(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@Positive @PathVariable(name = "userId") Long userId,
                                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating event by userId={}, {}", userId, newEventDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (LocalDateTime.parse(newEventDto.getEventDate(), formatter)
                .isBefore(LocalDateTime.now().plus(2, ChronoUnit.HOURS))) {
            throw new ValidationException("Время начала события раньше чем через 2 часа");
        } else {
            return new ResponseEntity<>(eventService.createEvent(userId, newEventDto), HttpStatus.CREATED);
        }
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto findEventByUserAndEventId(@Positive @PathVariable(name = "userId") Long userId,
                                                   @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("Get event by userId={} and eventId={}", userId, eventId);
        return eventService.findEventByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventById(@Positive @PathVariable(name = "userId") Long userId,
                                         @Positive @PathVariable(name = "eventId") Long eventId,
                                         @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Update event {} by userId={} and eventId={}", updateEventUserRequest, userId, eventId);
        return eventService.updateEventByUserAndEventId(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> findEventsByParam(
            @RequestParam(name = "users", defaultValue = "0") List<Long> users,
            @RequestParam(name = "states", defaultValue = "All") List<String> states,
            @RequestParam(name = "categories", defaultValue = "0") List<Long> categories,
            @RequestParam(name = "rangeStart", defaultValue = "false") String rangeStart,
            @RequestParam(name = "rangeEnd", defaultValue = "false") String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
            ) {
        log.info("Get events by users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findEventsByParam(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Update event {} by eventId={}", updateEventAdminRequest, eventId);
        return eventService.updateEventByEventId(eventId, updateEventAdminRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> findEvents(
            @RequestParam(name = "text", defaultValue = "false") String text,
            @RequestParam(name = "categories", defaultValue = "0") List<Long> categories,
            @RequestParam(name = "paid", defaultValue = "false") Boolean paid,
            @RequestParam(name = "rangeStart", defaultValue = "false") String rangeStart,
            @RequestParam(name = "rangeEnd", defaultValue = "false") String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "false") String sort,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
            ) {
        log.info("Get events by text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                " sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@Positive @PathVariable(name = "id") Long id,
                                      HttpServletRequest request) {
        log.info("Get event by id={}", id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                0,
                "main",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter));
        statsClient.postStats(endpointHitDto);
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);
        String uri = request.getRequestURI();
        List<String> uris = new ArrayList<>();
        uris.add(uri);
        ResponseEntity<Object[]> view = statsClient.get(
                start,
                end,
                uri,
                "true"
        );
        Object[] objects = view.getBody();
        ViewStatsDto viewStatsDto;
        if (objects != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<Long> hits = Arrays.stream(objects)
                    .map(object -> mapper.convertValue(object, ViewStatsDto.class))
                    .map(ViewStatsDto::getHits)
                    .collect(Collectors.toList());
            viewStatsDto = new ViewStatsDto("main", uri, hits.get(0));
        } else {
            viewStatsDto = new ViewStatsDto("main", uri, 0L);
        }
        return eventService.findEventById(id, viewStatsDto);
    }
}
