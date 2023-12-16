package org.example.ewm.categories;

import org.example.ewm.categories.dto.CategoryDto;
import org.example.ewm.categories.dto.NewCategoryDto;
import org.example.ewm.categories.model.Category;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    public static Category fromNewCategoryDto(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> toCategoriesDto(Page<Category> categoryPage) {
        List<CategoryDto> categoriesDto = new ArrayList<>();
        for (Category category : categoryPage) {
            categoriesDto.add(toCategoryDto(category));
        }
        return categoriesDto;
    }
}
