package org.example.ewm.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.categories.dto.CategoryDto;
import org.example.ewm.categories.dto.NewCategoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Creating category {}", newCategoryDto);
        return new ResponseEntity<>(categoryService.createCategory(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<Objects> deleteCategory(@Positive @PathVariable(name = "catId") Long catId) {
        log.info("Delete category id={}", catId);
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@Positive @PathVariable(name = "catId") Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Update category {}, id={}", categoryDto, catId);
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findCategories(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get categories from={}, size={}", from, size);
        return categoryService.findCategories(from, size);
    }

    @GetMapping(value = "/categories/{catId}")
    public CategoryDto findCategoryById(@Positive @PathVariable(name = "catId") Long catId) {
        log.info("Get category by id {}", catId);
        return categoryService.findCategoryById(catId);
    }
}
