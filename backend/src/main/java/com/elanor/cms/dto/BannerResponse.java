package com.elanor.cms.dto;

import com.elanor.cms.entity.Banner;

import java.time.Instant;
import java.util.UUID;

public class BannerResponse {

    private UUID id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String linkUrl;
    private String placement;
    private int displayOrder;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public BannerResponse() {}

    public BannerResponse(Banner banner) {
        if (banner != null) {
            this.id = banner.getId();
            this.title = banner.getTitle();
            this.subtitle = banner.getSubtitle();
            this.imageUrl = banner.getImageUrl();
            this.linkUrl = banner.getLinkUrl();
            this.placement = banner.getPlacement();
            this.displayOrder = banner.getDisplayOrder();
            this.active = banner.isActive();
            this.createdAt = banner.getCreatedAt();
            this.updatedAt = banner.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
