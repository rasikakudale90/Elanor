package com.elanor.category.repository;

import com.elanor.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentIsNullAndActiveTrueOrderByDisplayOrderAsc();
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();
    List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsBySlug(String slug);
}
