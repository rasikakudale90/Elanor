package com.elanor.category.service;

import com.elanor.category.dto.*;
import com.elanor.category.entity.Category;
import com.elanor.category.repository.CategoryRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoryTree() {
        List<Category> allActive = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
        Map<UUID, List<Category>> childrenMap = allActive.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        List<CategoryDto> rootDtos = new ArrayList<>();
        for (Category root : allActive) {
            if (root.getParent() == null) {
                rootDtos.add(buildTree(root, childrenMap));
            }
        }
        return rootDtos;
    }

    @Transactional(readOnly = true)
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with slug: " + slug));
        
        List<Category> allActive = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
        Map<UUID, List<Category>> childrenMap = allActive.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return buildTree(category, childrenMap);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? generateSlug(request.getSlug())
                : generateSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Category with slug '" + slug + "' already exists.");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Parent category not found."));
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(request.isActive());
        category.setParent(parent);

        if (parent != null) {
            parent.getSubcategories().add(category);
        }

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Transactional
    public CategoryDto updateCategory(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found."));

        Category parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Category cannot be its own parent.");
            }
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Parent category not found."));
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(request.isActive());
        category.setParent(parent);

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found."));
        categoryRepository.delete(category);
    }

    private CategoryDto mapToDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                category.getDisplayOrder(),
                category.isActive()
        );
    }

    private CategoryDto buildTree(Category category, Map<UUID, List<Category>> childrenMap) {
        CategoryDto dto = mapToDto(category);
        List<Category> children = childrenMap.getOrDefault(category.getId(), List.of());
        dto.setSubcategories(children.stream()
                .map(child -> buildTree(child, childrenMap))
                .collect(Collectors.toList()));
        return dto;
    }

    private String generateSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
