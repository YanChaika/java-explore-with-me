package org.example.ewm.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.categories.dto.CategoryDto;
import org.example.ewm.categories.dto.NewCategoryDto;
import org.example.ewm.categories.model.Category;
import org.example.ewm.event.EventRepository;
import org.example.ewm.exception.ConflictException;
import org.example.ewm.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление категории");
        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category category = CategoryMapper.fromNewCategoryDto(newCategoryDto);
        Category categoryWithId = categoryRepository.save(category);
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(categoryWithId);
        List<Category> all = categoryRepository.findAll();
        return categoryDto;
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Удаление категории");
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с id " + catId + " не найдена")
        );
        if (eventRepository.findAllByCategoryId(catId).isEmpty()) {
            categoryRepository.deleteById(catId);
            categoryRepository.findAll();
            categoryRepository.findById(catId);
        } else {
            throw new ConflictException("К категории привязаны события");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Обновление категории");
        Category categoryToUpdate = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с id " + catId + " не найдена")
        );
        if ((categoryRepository.findByName(categoryDto.getName()) != null)
                && (!categoryRepository.findByName(categoryDto.getName()).getId().equals(catId))) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        categoryToUpdate.setName(categoryDto.getName());
        Category category = categoryRepository.saveAndFlush(categoryToUpdate);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> findCategories(Integer from, Integer size) {
        log.info("Поиск категории");
        Pageable pageRequest = PageRequest.of(from, size);
        Page<Category> categories = categoryRepository.findAll(pageRequest);
        return CategoryMapper.toCategoriesDto(categories);
    }

    @Override
    public CategoryDto findCategoryById(Long catId) {
        log.info("Поиск категории c id " + catId);
        List<Category> all = categoryRepository.findAll();
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с id " + catId + " не найдена")
        );
        return CategoryMapper.toCategoryDto(category);
    }

}
