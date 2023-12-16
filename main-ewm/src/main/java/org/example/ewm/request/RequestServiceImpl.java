package org.example.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.enums.RequestStatus;
import org.example.ewm.enums.State;
import org.example.ewm.event.EventRepository;
import org.example.ewm.event.model.Event;
import org.example.ewm.exception.ConflictException;
import org.example.ewm.exception.NotFoundException;
import org.example.ewm.request.dto.EventRequestStatusUpdateRequest;
import org.example.ewm.request.dto.EventRequestStatusUpdateResult;
import org.example.ewm.request.dto.ParticipationRequestDto;
import org.example.ewm.request.model.Request;
import org.example.ewm.user.UserRepository;
import org.example.ewm.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> findRequests(Long userId) {
        log.info("Поиск запросов");
        isUserExist(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return RequestMapper.toParticipationRequestsDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Добавление запроса");
        List<Request> requests = requestRepository.findAll();
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId) != null) {
            throw new ConflictException("Запрос уже существует");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id" + eventId + " не найдено")
        );
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Запрос от инициатора события");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }
        if (event.getParticipantLimit() > 0) {
            if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                throw new ConflictException("Лимит участников исчерпан");
            }
        }
        LocalDateTime createdTime = LocalDateTime.now();
        RequestStatus status;
        if (event.isRequestModeration()) {
            status = RequestStatus.PENDING;
        } else {
            status = RequestStatus.CONFIRMED;
        }
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        Request request = new Request(
                0L,
                createdTime,
                event,
                user,
                status
        );
        Request requestWithId = requestRepository.save(request);
        updateEventConfirmedRequest(event);
        return RequestMapper.toParticipationRequestDto(requestWithId);
    }

    private void updateEventConfirmedRequest(Event event) {
        List<Request> requestsToCount = requestRepository.findAllByEventId(event.getId());
        int countConfirmedRequests = event.getConfirmedRequests();
        for (Request request1 : requestsToCount) {
            if (request1.getStatus().equals(RequestStatus.CONFIRMED)) {
                countConfirmedRequests++;
            }
        }
        event.setConfirmedRequests(countConfirmedRequests);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена запроса");
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Событие с id" + requestId + " не найдено")
        );
        List<Request> test = requestRepository.findAll();
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.delete(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByUserIdAndEventId(Long userId, Long eventId) {
        log.info("Поиск запроса");
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id" + eventId + " не найдено")
        );
        List<Request> toReturn = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        if (!requests.isEmpty()) {
            for (Request request : requests) {
                if (request.getEvent().getInitiator().getId().equals(userId)) {
                    toReturn.add(request);
                }
            }
        }
        return RequestMapper.toParticipationRequestsDto(toReturn);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsInPending(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId
    ) {
        log.info("Обновление статуса запроса запроса");
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id" + eventId + " не найдено")
        );
        List<Request> testRequests = requestRepository.findAll();
        List<Request> updaterRequests = new ArrayList<>();
        for (Long requestId : eventRequestStatusUpdateRequest.getRequestIds()) {
            Request request = requestRepository.findById(requestId).orElseThrow(
                    () -> new NotFoundException("Запрос с id" + requestId + " не найден")
            );
            if (request.getStatus().equals(RequestStatus.PENDING)) {
                if (eventRequestStatusUpdateRequest.getStatus().equals("CONFIRMED")) {
                    if (event.getParticipantLimit() > 0) {
                        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                            throw new ConflictException("Лимит участников исчерпан");
                        }
                    }
                    request.setStatus(RequestStatus.CONFIRMED);
                    eventRepository.saveAndFlush(event);
                    updateEventConfirmedRequest(event);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                }
            } else {
                throw new ConflictException("Заявка уже принята/отменена");
            }
            requestRepository.saveAndFlush(request);
            updaterRequests.add(request);
        }
        List<ParticipationRequestDto> requestDtos = RequestMapper.toParticipationRequestsDto(updaterRequests);
        return new EventRequestStatusUpdateResult(requestDtos, requestDtos);
    }

    private void isUserExist(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id" + userId + " не найден")
        );
    }
}
