package org.example.explorewithme.model;

import org.example.explorewithme.dto.EndpointHitDto;

import java.util.ArrayList;
import java.util.List;

public class EndpointHitMapper {

    public static EndpointHit fromEndpointHitDto(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public static List<EndpointHitDto> toEndpointHitsDto(List<EndpointHit> endpointHits) {
        List<EndpointHitDto> endpointHitDtos = new ArrayList<>();
        for (EndpointHit endpointHit : endpointHits) {
            endpointHitDtos.add(EndpointHitMapper.toEndpointHitDto(endpointHit));
        }
        return endpointHitDtos;
    }
}
