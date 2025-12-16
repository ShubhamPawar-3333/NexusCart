package com.nexuscart.product.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.product.entity.Category;
import com.nexuscart.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories")
    public ResponseEntity<ApiResponse<List<Category>>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable UUID id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get subcategories")
    public ResponseEntity<ApiResponse<List<Category>>> getSubcategories(@PathVariable UUID id) {
        List<Category> subcategories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(ApiResponse.success(subcategories));
    }

    @PostMapping
    @Operation(summary = "Create category")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Category created"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable UUID id, @RequestBody Category updates) {
        Category updated = categoryService.updateCategory(id, updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Category updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted"));
    }
}
