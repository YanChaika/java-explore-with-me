package org.example.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.model.EndpointHit;
import org.example.explorewithme.model.EndpointHitMapper;
import org.example.explorewithme.repository.StatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public List<ViewStatsDto> findStats(String start, String end, List<String> uris, String unique) {
        log.info("Получение статистики");
        List<EndpointHit> all = statsRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();
        List<ViewStatsDto> viewStatsDtosByAllUri = new ArrayList<>();
        if ((!uris.get(0).equals("false")) && (unique.equals("true"))) {
            for (String s : uris) {
                viewStatsDtos = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(s, startTime, endTime);
                viewStatsDtosByAllUri.addAll(viewStatsDtos);
            }
        } else if (!uris.get(0).equals("false")) {
            for (String s : uris) {
                viewStatsDtos = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfter(s, startTime, endTime);
                viewStatsDtosByAllUri.addAll(viewStatsDtos);
            }
        } else if (unique.equals("true")) {
            viewStatsDtos = statsRepository.findAllByStartIsBeforeAndEndIsAfterUnique(startTime, endTime);
            viewStatsDtosByAllUri.addAll(viewStatsDtos);
        } else {
            viewStatsDtos = statsRepository.findAllByStartIsBeforeAndEndIsAfter(startTime, endTime);
            viewStatsDtosByAllUri.addAll(viewStatsDtos);
        }
        Collections.sort(viewStatsDtosByAllUri, new Comparator<ViewStatsDto>() {
            public int compare(ViewStatsDto o1, ViewStatsDto o2) {
                return o2.getHits().compareTo(o1.getHits());
            }
        });
        return viewStatsDtosByAllUri;
    }


    @Override
    public EndpointHitDto createStats(EndpointHitDto endpointHitDto) {
        log.info("Добавление статистики");
        EndpointHit endpointHit = EndpointHitMapper.fromEndpointHitDto(endpointHitDto);
        EndpointHit endpointHitWithId = statsRepository.save(endpointHit);
        EndpointHitDto endpointHitDtoWithId = EndpointHitMapper.toEndpointHitDto(endpointHitWithId);
        return endpointHitDtoWithId;
    }
}
