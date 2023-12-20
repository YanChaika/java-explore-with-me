package org.example.ewm.request;

import org.example.ewm.request.dto.EventRequestStatusUpdateRequest;
import org.example.ewm.request.dto.EventRequestStatusUpdateResult;
import org.example.ewm.request.dto.ParticipationRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface RequestService {

    List<ParticipationRequestDto> findRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsInPending(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long usrId,
            Long eventId
    );
}
