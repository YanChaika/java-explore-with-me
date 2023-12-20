package org.example.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.request.dto.EventRequestStatusUpdateRequest;
import org.example.ewm.request.dto.EventRequestStatusUpdateResult;
import org.example.ewm.request.dto.ParticipationRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> findRequests(@Positive @PathVariable(name = "userId") Long userId) {
        log.info("Get requests by user={}", userId);
        return requestService.findRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@Positive @PathVariable(name = "userId") Long userId,
                                                                 @Positive @RequestParam(name = "eventId") Long eventId
    ) {
        log.info("Create request by user={}, event={}", userId, eventId);
        return new ResponseEntity<>(requestService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@Positive @PathVariable(name = "userId") Long userId,
                                                 @Positive @PathVariable(name = "requestId") Long requestId) {
        log.info("Cancel request by user={}, event={}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestsByUserIdAndEventId(
            @Positive @PathVariable(name = "userId") Long userId,
            @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("Get requests by userId={} and eventId={}", userId, eventId);
        return requestService.findRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateResult(
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            @Positive @PathVariable(name = "userId") Long userId,
            @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("Update request {}, by user={}, event={}", eventRequestStatusUpdateRequest, userId, eventId);
        return requestService.updateRequestsInPending(eventRequestStatusUpdateRequest, userId, eventId);
    }
}