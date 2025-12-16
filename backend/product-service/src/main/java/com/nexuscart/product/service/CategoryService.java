package com.nexuscart.product.service;

import com.nexuscart.product.entity.Category;
import com.nexuscart.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAllActiveCategories();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue();
    }

    public List<Category> getSubcategories(UUID parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId);
    }

    public Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists");
        }
        log.info("Creating category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(UUID id, Category updates) {
        Category existing = getCategory(id);

        if (updates.getName() != null)
            existing.setName(updates.getName());
        if (updates.getDescription() != null)
            existing.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null)
            existing.setImageUrl(updates.getImageUrl());
        existing.setDisplayOrder(updates.getDisplayOrder());

        log.info("Updated category: {}", id);
        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategory(id);
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Deactivated category: {}", id);
    }
}
