package org.example.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.compilation.dto.CompilationDto;
import org.example.ewm.compilation.dto.NewCompilationDto;
import org.example.ewm.compilation.dto.UpdateCompilationRequest;
import org.example.ewm.compilation.model.Compilation;
import org.example.ewm.event.EventRepository;
import org.example.ewm.event.model.Event;
import org.example.ewm.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService{

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Поиск подборок");
        Pageable pageRequest = PageRequest.of(from, size);
        if (pinned) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned);
            if ((size + from) > compilations.size()) {
                size = compilations.size() - from;
            }
            List<Compilation> compilationList = compilations.subList(from, size);
            return CompilationMapper.toCompilationsDto(compilationList);
        } else {
            Page<Compilation> compilationPage = compilationRepository.findAll(pageRequest);
            return CompilationMapper.toCompilationsDto(compilationPage);
        }
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        log.info("Поиск подборки по id");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id " + compId + " не найдена")
        );
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавление подборки по id");
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            if (!newCompilationDto.getEvents().isEmpty()) {
                for (Long event : newCompilationDto.getEvents()) {
                    events.add(eventRepository.findById(event).get());
                }
            }
        }
        Compilation compilation = CompilationMapper.fromNewCompilationDto(newCompilationDto, events);
        Compilation compilationWithId = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationWithId);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        log.info("Удаление подборки по id");
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id " + compId + " не найдена")
        );
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновление подборки по id");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id " + compId + " не найдена")
        );
        List<Event> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            if (!updateCompilationRequest.getEvents().isEmpty()) {
                for (Long event : updateCompilationRequest.getEvents()) {
                    events.add(eventRepository.findById(event).get());
                }
            }
        }
        compilation.setEvents(events);
        compilation.setPinned(updateCompilationRequest.isPinned());
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }
}
