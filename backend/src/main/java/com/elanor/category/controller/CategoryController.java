package com.elanor.category.controller;

import com.elanor.category.dto.CategoryDto;
import com.elanor.category.service.CategoryService;
import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category", description = "Public Category Navigation APIs")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get complete hierarchical category tree")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategoryTree() {
        List<CategoryDto> categories = categoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get category details by slug")
    public ResponseEntity<ApiResponse<CategoryDto>> getBySlug(@PathVariable String slug) {
        CategoryDto category = categoryService.getBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(category));
    }
}
