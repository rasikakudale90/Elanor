package com.elanor.category.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryDto {

    private UUID id;
    private UUID parentId;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private int displayOrder;
    private boolean active;
    private List<CategoryDto> subcategories = new ArrayList<>();

    public CategoryDto() {}

    public CategoryDto(UUID id, UUID parentId, String name, String slug, String description, String imageUrl, int displayOrder, boolean active) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<CategoryDto> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryDto> subcategories) {
        this.subcategories = subcategories;
    }
}
