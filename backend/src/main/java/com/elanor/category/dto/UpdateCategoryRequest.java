package com.elanor.category.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class UpdateCategoryRequest {

    private UUID parentId;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String imageUrl;
    private int displayOrder;
    private boolean active;

    public UpdateCategoryRequest() {}

    public UpdateCategoryRequest(UUID parentId, String name, String description, String imageUrl, int displayOrder, boolean active) {
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.active = active;
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
}
