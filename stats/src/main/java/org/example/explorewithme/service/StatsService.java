package org.example.explorewithme.service;

import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StatsService {

    Optional<ViewStatsDto> findStats(String start, String end, String uris, String unique);

    Optional<EndpointHitDto> createStats(EndpointHitDto endpointHit);
}
