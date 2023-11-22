package org.example.explorewithme.service;

import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface StatsService {

    @Transactional
    List<ViewStatsDto> findStats(String start, String end, List<String> uris, String unique);

    @Transactional
    EndpointHitDto createStats(EndpointHitDto endpointHit);
}
