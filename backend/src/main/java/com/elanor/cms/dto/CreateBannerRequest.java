package com.elanor.cms.dto;

import com.elanor.cms.entity.Banner;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class CreateBannerRequest {

    @NotBlank(message = "Banner title is required")
    private String title;

    private String subtitle;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String linkUrl;

    private String placement = "HOME_HERO";

    private int displayOrder = 0;

    private boolean active = true;

    public CreateBannerRequest() {}

    public CreateBannerRequest(String title, String subtitle, String imageUrl, String linkUrl, String placement, int displayOrder, boolean active) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.placement = placement;
        this.displayOrder = displayOrder;
        this.active = active;
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
}
