package org.example.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.compilation.dto.CompilationDto;
import org.example.ewm.compilation.dto.NewCompilationDto;
import org.example.ewm.compilation.dto.UpdateCompilationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> findCompilations(
            @RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get compilations pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.findCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationById(@Positive @PathVariable(name = "compId") Long compId) {
        log.info("Get compilation by id {}", compId);
        return compilationService.findCompilationById(compId);
    }

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Create compilation {}", newCompilationDto);
        return new ResponseEntity<>(compilationService.createCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<String> deleteCompilationById(@Positive @PathVariable(name = "compId") Long compId) {
        log.info("Delete compilation by id {}", compId);
        compilationService.deleteCompilationById(compId);
        return new ResponseEntity<>("Подборка удалена", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@Positive @PathVariable(name = "compId") Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Update compilation {}", updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }
}
