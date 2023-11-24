package org.example.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestParam String start,
                                                            @RequestParam String end,
                                                            @RequestParam(name = "uris", defaultValue = "false")
                                                                List<String> uris,
                                                            @RequestParam(name = "unique", defaultValue = "false") String unique
    ) {
        return statsService.findStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> postStats(@RequestBody EndpointHitDto endpointHit) {
        return ResponseEntity.ok(statsService.createStats(endpointHit));
    }
}
