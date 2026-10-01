package com.elanor.collection.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCollectionRequest {

    @NotBlank(message = "Collection name is required")
    private String name;

    private String slug;
    private String description;
    private String bannerUrl;
    private boolean active = true;

    public CreateCollectionRequest() {}

    public CreateCollectionRequest(String name, String slug, String description, String bannerUrl, boolean active) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.bannerUrl = bannerUrl;
        this.active = active;
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

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
