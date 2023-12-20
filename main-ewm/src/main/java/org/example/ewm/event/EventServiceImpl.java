package org.example.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.categories.CategoryRepository;
import org.example.ewm.categories.model.Category;
import org.example.ewm.enums.RequestStatus;
import org.example.ewm.enums.State;
import org.example.ewm.enums.StateAction;
import org.example.ewm.event.dto.*;
import org.example.ewm.event.model.Event;
import org.example.ewm.exception.ConflictException;
import org.example.ewm.exception.NotFoundException;
import org.example.ewm.exception.ValidationException;
import org.example.ewm.location.Location;
import org.example.ewm.location.LocationRepository;
import org.example.ewm.request.RequestRepository;
import org.example.ewm.request.model.Request;
import org.example.ewm.user.UserRepository;
import org.example.ewm.user.model.User;
import org.example.explorewithme.dto.ViewStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> findEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Поиск события");
        isUserExist(userId);
        List<Event> eventsAll = eventRepository.findAll();
        Pageable pageRequest = PageRequest.of(from, size);
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        List<Event> eventsConvert = events.toList();
        List<EventShortDto> eventsShortDto = EventMapper.toEventsShortDto(eventsConvert);
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события");
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
        Category category = categoryRepository.findById(newEventDto.getCategory()).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setConfirmedRequests(0);
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter));
        event.setInitiator(user);
        event.setPaid(newEventDto.isPaid());
        event.setTitle(newEventDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setLocation(newEventDto.getLocation());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setParticipantCount(0L);
        event.setPublishedOn(LocalDateTime.now());
        if ((newEventDto.getRequestModeration() == null)
                || (newEventDto.getRequestModeration())) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(false);
        }
        event.setState(State.PENDING);
        Location location = newEventDto.getLocation();
        locationRepository.save(location);
        Event eventWithId = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventWithId);
        return eventFullDto;
    }

    @Override
    public EventFullDto findEventByUserAndEventId(Long userId, Long eventId) {
        log.info("Поиск события по id");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserAndEventId(
            Long userId,
            Long eventId,
            UpdateEventUserRequest updateEventUserRequest
    ) {
        log.info("Обновление события по id");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (event.getEventDate().isBefore(LocalDateTime.now().plus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("Дата начала изменяемого события раньше чем через 2 часа");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventUserRequest.getEventDate(), formatter)
                    .isBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS))) {
                throw new ValidationException("Время начала нового события слишком рано");
            }
        }
        if (event.getState() != null) {
            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Изменяемое событие в статусе опубликовано");
            }
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != 0) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).get();
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate()));
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.isPaid()) {
            event.setPaid(true);
        }
        if (updateEventUserRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            if (updateEventUserRequest.getRequestModeration().equals("true")) {
                event.setRequestModeration(true);
            } else {
                event.setRequestModeration(false);
            }
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.toString())) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        Event eventWithId = eventRepository.saveAndFlush(event);
        return EventMapper.toEventFullDto(eventWithId);
    }

    @Override
    public List<EventFullDto> findEventsByParam(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size
    ) {
        log.info("Поиск события по параметрам");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pageable pageRequest = PageRequest.of(from, size);
        Set<Event> sortedEvents = new HashSet<>();
        List<Event> toSorted = eventRepository.findAll();
        if (users.get(0) != 0) {
            for (Long user : users) {
                for (Event event : toSorted) {
                    if (user.equals(event.getInitiator().getId())) {
                        sortedEvents.add(event);
                    }
                }
            }
        }
        if (!states.get(0).equals("All")) {
            for (String state : states) {
                if (sortedEvents.isEmpty()) {
                    for (Event event : toSorted) {
                        if (state.equals(event.getState().toString())) {
                            sortedEvents.add(event);
                        }
                    }
                } else {
                    sortedEvents.removeIf(sortedEvent -> !state.equals(sortedEvent.getState().toString()));

                }
            }
        }
        if (categories.get(0) != 0) {
            for (Long category : categories) {
                if (sortedEvents.isEmpty()) {
                    for (Event event : toSorted) {
                        if (category.equals(event.getCategory().getId())) {
                            sortedEvents.add(event);
                        }
                    }
                } else {
                    sortedEvents.removeIf(sortedEvent -> !category.equals(sortedEvent.getCategory().getId()));

                }
            }
        }
        if (!rangeStart.equals("false")) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (LocalDateTime.parse(rangeStart, formatter).isBefore(event.getEventDate())) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                sortedEvents.removeIf(
                        sortedEvent -> !LocalDateTime.parse(rangeStart, formatter).isBefore(sortedEvent.getEventDate())
                );
            }
        }
        if (!rangeEnd.equals("false")) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (LocalDateTime.parse(rangeEnd, formatter).isAfter(event.getEventDate())) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                sortedEvents.removeIf(
                        sortedEvent -> !LocalDateTime.parse(rangeEnd, formatter).isAfter((sortedEvent.getEventDate()))
                );
            }
        }
        List<EventFullDto> eventFullDtos;
        if (sortedEvents.isEmpty()) {
            for (Event event : toSorted) {
                event.setConfirmedRequests(updateConfirmedRequest(event));
            }
            eventFullDtos = EventMapper.toEventsFullDto(toSorted);
        } else {
            for (Event sortedEvent : sortedEvents) {
                sortedEvent.setConfirmedRequests(updateConfirmedRequest(sortedEvent));
            }
            eventFullDtos = EventMapper.toEventsFullDto(sortedEvents);
        }
        if ((size + from) > eventFullDtos.size()) {
            size = eventFullDtos.size() - from;
        }
        return eventFullDtos.subList(from, size);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByEventId(Long eventId, UpdateEventAdminRequest updateEvent) {
        log.info("Обновление события по параметрам");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id" + eventId + " не найдено")
        );
        List<Category> categoriesToDebug = categoryRepository.findAll();
        if (eventToUpdate.getEventDate().isBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS))) {
            throw new ConflictException("Время начала события слишком рано");
        }
        if (updateEvent.getEventDate() != null) {
            if (LocalDateTime.parse(updateEvent.getEventDate(), formatter)
                    .isBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS))) {
                throw new ValidationException("Время начала нового события слишком рано");
            }
        }
        if (updateEvent.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            eventToUpdate.setCategory(categoryRepository.findById(updateEvent.getCategory()).get());
        }
        if (updateEvent.getDescription() != null) {
            eventToUpdate.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            eventToUpdate.setEventDate(LocalDateTime.parse(updateEvent.getEventDate(), formatter));
        }
        if (updateEvent.getLocation() != null) {
            eventToUpdate.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            eventToUpdate.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            if (updateEvent.getRequestModeration()) {
                eventToUpdate.setRequestModeration(true);
            }
        }
        if (updateEvent.getTitle() != null) {
            eventToUpdate.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())) {
                if (eventToUpdate.getState().equals(State.PENDING)) {
                    eventToUpdate.setState(State.PUBLISHED);
                } else {
                    throw new ConflictException("Событие не в статусе ожидает публикации");
                }
            } else if (updateEvent.getStateAction().equals(StateAction.REJECT_EVENT.toString())) {
                if (!eventToUpdate.getState().equals(State.PUBLISHED)) {
                    eventToUpdate.setState(State.CANCELED);
                } else {
                    throw new ConflictException("Событие уже опубликовано");
                }
            }
        }
        Event event = eventRepository.saveAndFlush(eventToUpdate);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size
    ) {
        log.info("Поиск события по параметрам");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pageable pageRequest = PageRequest.of(from, size);
        Set<Event> sortedEvents = new HashSet<>();
        List<Event> toSortedPublished = new ArrayList<>();
        List<Event> toSorted = eventRepository.findAll();
        if (!text.equals("false")) {
            toSorted = eventRepository.findEventsByQuery(text);
        }
        if (text.length() <= 1) {
            throw new ValidationException("Поле text 1 символ");
        }
        if (categories.get(0) != 0) {
            for (Long category : categories) {
                sortedEvents.addAll(eventRepository.findAllByCategoryId(category));
            }
        }
        if (paid) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (event.isPaid()) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                for (Event event : toSorted) {
                    if (!event.isPaid()) {
                        sortedEvents.remove(event);
                    }
                }
            }
        }
        if (!rangeStart.equals("false")) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (LocalDateTime.parse(rangeStart, formatter).isBefore(event.getEventDate())) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                sortedEvents.removeIf(
                        sortedEvent -> !LocalDateTime.parse(rangeStart, formatter).isBefore(sortedEvent.getEventDate())
                );
            }
        }
        if (!rangeEnd.equals("false")) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (LocalDateTime.parse(rangeEnd, formatter).isAfter(event.getEventDate())) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                sortedEvents.removeIf(
                        sortedEvent -> !LocalDateTime.parse(rangeEnd, formatter).isAfter((sortedEvent.getEventDate()))
                );
            }
        }
        if (onlyAvailable) {
            if (sortedEvents.isEmpty()) {
                for (Event event : toSorted) {
                    if (event.getParticipantLimit() > 0) {
                        sortedEvents.add(event);
                    }
                }
            } else {
                sortedEvents.removeIf(sortedEvent -> sortedEvent.getParticipantLimit() <= 0);
            }
        }
        if (sortedEvents.isEmpty()) {
            for (Event event : toSorted) {
                if ((event.getState().equals(State.PUBLISHED)) && (event.getEventDate().isAfter(LocalDateTime.now()))) {
                    toSortedPublished.add(event);
                }
            }
        } else {
            for (Event sortedEvent : sortedEvents) {
                if ((sortedEvent.getState().equals(State.PUBLISHED)) &&
                        (sortedEvent.getEventDate().isAfter(LocalDateTime.now()))) {
                    toSortedPublished.add(sortedEvent);
                }
            }
        }
        if ((size + from) > toSortedPublished.size()) {
            size = toSortedPublished.size() - from;
        }
        List<Event> pageEvents = toSortedPublished.subList(from, size);
        List<EventShortDto> eventShortDtos = EventMapper.toEventsShortDto(pageEvents);
        return eventShortDtos;
    }

    @Override
    public EventFullDto findEventById(Long id, ViewStatsDto viewStatsDto) {
        log.info("Поиск события по id");
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Событие с id" + id + " не найдено")
        );
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id" + id + " не опубликовано");
        }
        event.setViews(viewStatsDto.getHits());
        return EventMapper.toEventFullDto(event);
    }

    private void isUserExist(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
    }

    private int updateConfirmedRequest(Event event) {
        List<Request> requests = requestRepository.findAllByEventId(event.getId());
        int count = 0;
        for (Request request : requests) {
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                count++;
            }
        }
        return count;
    }
}
