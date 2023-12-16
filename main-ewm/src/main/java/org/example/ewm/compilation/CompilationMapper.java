package org.example.ewm.compilation;

import org.example.ewm.compilation.dto.CompilationDto;
import org.example.ewm.compilation.dto.NewCompilationDto;
import org.example.ewm.compilation.model.Compilation;
import org.example.ewm.event.EventMapper;
import org.example.ewm.event.model.Event;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                EventMapper.toEventsShortDto(compilation.getEvents()),
                compilation.getId(),
                compilation.isPinned(),
                compilation.getTitle()
        );
    }

    public static List<CompilationDto> toCompilationsDto(Page<Compilation> compilations) {
        List<CompilationDto> compilationsDto = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationsDto.add(toCompilationDto(compilation));
        }
        return compilationsDto;
    }

    public static List<CompilationDto> toCompilationsDto(List<Compilation> compilations) {
        List<CompilationDto> compilationsDto = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationsDto.add(toCompilationDto(compilation));
        }
        return compilationsDto;
    }

    public static Compilation fromNewCompilationDto(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(
                0,
                newCompilationDto.isPinned(),
                newCompilationDto.getTitle(),
                events
        );
    }
}
