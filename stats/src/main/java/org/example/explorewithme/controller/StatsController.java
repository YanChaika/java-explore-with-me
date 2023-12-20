package org.example.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.exception.ValidationException;
import org.example.explorewithme.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestParam String start,
                                                            @RequestParam String end,
                                                            @RequestParam(name = "uris", defaultValue = "false")
                                                                List<String> uris,
                                                            @RequestParam(name = "unique", defaultValue = "false")
                                                                String unique
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (LocalDateTime.parse(start, formatter).isAfter(LocalDateTime.parse(end, formatter))) {
            throw new ValidationException("Start time is after end time");
        }
        return statsService.findStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> postStats(@RequestBody EndpointHitDto endpointHit) {
        return new ResponseEntity<>(statsService.createStats(endpointHit), HttpStatus.CREATED);
    }
}
