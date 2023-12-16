package org.example.ewm.request;

import org.example.ewm.request.dto.ParticipationRequestDto;
import org.example.ewm.request.model.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getCreated().toString(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().toString()
        );
    }

    public static List<ParticipationRequestDto> toParticipationRequestsDto(List<Request> requests) {
        List<ParticipationRequestDto> participationRequestsDto = new ArrayList<>();
        for (Request request : requests) {
            participationRequestsDto.add(toParticipationRequestDto(request));
        }
        return participationRequestsDto;
    }
}
