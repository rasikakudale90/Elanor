package com.elanor.collection.dto;

import java.util.UUID;

public class CollectionDto {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String bannerUrl;
    private boolean active;

    public CollectionDto() {}

    public CollectionDto(UUID id, String name, String slug, String description, String bannerUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.bannerUrl = bannerUrl;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
