package com.elanor.collection.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateCollectionRequest {

    @NotBlank(message = "Collection name is required")
    private String name;

    private String description;
    private String bannerUrl;
    private boolean active;

    public UpdateCollectionRequest() {}

    public UpdateCollectionRequest(String name, String description, String bannerUrl, boolean active) {
        this.name = name;
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
