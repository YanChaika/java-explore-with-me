package org.example.ewm.compilation;

import org.example.ewm.compilation.dto.CompilationDto;
import org.example.ewm.compilation.dto.NewCompilationDto;
import org.example.ewm.compilation.dto.UpdateCompilationRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CompilationService {

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
