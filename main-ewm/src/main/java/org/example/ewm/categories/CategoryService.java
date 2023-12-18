package org.example.ewm.categories;

import org.example.ewm.categories.dto.CategoryDto;
import org.example.ewm.categories.dto.NewCategoryDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategoryById(Long catId);

}
