package org.example.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.explorewithme.dto.EndpointHitDto;
import org.example.explorewithme.dto.ViewStatsDto;
import org.example.explorewithme.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<Optional<ViewStatsDto>> findStats(@NotNull @RequestParam String start,
                                                            @NotNull @RequestParam String end,
                                                            @RequestParam(name = "uris", defaultValue = "false")
                                                                List<String> uris,
                                                            @RequestParam(name = "unique", defaultValue = "false") boolean unique
    ) {
        //return ResponseEntity.ok(statsService.findStats(start, end, uris, unique));
        return ResponseEntity.ok(statsService.findStats(start, end, uris, unique));
    }

    @PostMapping("/hit")
    public ResponseEntity<Optional<EndpointHitDto>> postStats(@RequestBody @Valid EndpointHitDto endpointHit) {
        return ResponseEntity.ok(statsService.createStats(endpointHit));
    }
}
