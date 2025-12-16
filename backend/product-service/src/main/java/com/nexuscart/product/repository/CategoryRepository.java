package com.nexuscart.product.repository;

import com.nexuscart.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);

    List<Category> findByParentIsNullAndActiveTrue();

    List<Category> findByParentIdAndActiveTrue(UUID parentId);

    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.displayOrder")
    List<Category> findAllActiveCategories();

    boolean existsByName(String name);
}
