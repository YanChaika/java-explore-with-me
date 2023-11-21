package org.example.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.model.EndpointHit;
import org.example.explorewithme.model.EndpointHitMapper;
import org.example.explorewithme.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public Optional<ViewStatsDto> findStats(String start, String end, String uris, String unique) {
        log.info("Получение статистики");
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        List<EndpointHit> endpointHits = new ArrayList<>();
        if ((!uris.isEmpty()) && (unique.equals("true"))) {
            endpointHits = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(uris, startTime, endTime);
            //endpointHits = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfter(startTime, endTime, uris);
        } else if (!uris.equals("false")) {
            endpointHits = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfter(uris, startTime, endTime);
        } else if (unique.equals("true")) {
            endpointHits = statsRepository.findAllByStartIsBeforeAndEndIsAfterUnique(startTime, endTime);
        } else {
            endpointHits = statsRepository.findAllByStartIsBeforeAndEndIsAfter(startTime, endTime);
        }
        ViewStatsDto viewStatsDto = new ViewStatsDto();
        for (EndpointHit endpointHit : endpointHits) {
            viewStatsDto.setApp(endpointHit.getApp());
            viewStatsDto.setUri(endpointHit.getUri());
        }
        viewStatsDto.setHits(endpointHits.size());
        return Optional.of(viewStatsDto);
    }

    @Override
    public Optional<ViewStatsDto> findStats(String start, String end, List<String> uris, boolean unique) {
        log.info("Получение статистики");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<EndpointHit> endpointHits = new ArrayList<>();
        if ((!uris.isEmpty()) && (unique)) {
            for (String s : uris) {
                endpointHits = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfterUnique(s, startTime, endTime);
            }
        } else if (!uris.isEmpty()) {
            for (String s : uris) {
                endpointHits = statsRepository.findAllByUrisFromStartIsBeforeAndEndIsAfter(s, startTime, endTime);
            }
        } else if (unique) {
            endpointHits = statsRepository.findAllByStartIsBeforeAndEndIsAfterUnique(startTime, endTime);
        } else {
            endpointHits = statsRepository.findAllByStartIsBeforeAndEndIsAfter(startTime, endTime);
        }
        ViewStatsDto viewStatsDto = new ViewStatsDto();
        for (EndpointHit endpointHit : endpointHits) {
            viewStatsDto.setApp(endpointHit.getApp());
            viewStatsDto.setUri(endpointHit.getUri());
        }
        viewStatsDto.setHits(endpointHits.size());
        return Optional.of(viewStatsDto);
    }


    @Override
    public Optional<EndpointHitDto> createStats(EndpointHitDto endpointHitDto) {
        log.info("Добавление статистики");
        EndpointHit endpointHit = EndpointHitMapper.fromEndpointHitDto(endpointHitDto);
        EndpointHit endpointHitWithId = statsRepository.save(endpointHit);
        EndpointHitDto endpointHitDtoWithId = EndpointHitMapper.toEndpointHitDto(endpointHitWithId);
        return Optional.of(endpointHitDtoWithId);
    }
}
