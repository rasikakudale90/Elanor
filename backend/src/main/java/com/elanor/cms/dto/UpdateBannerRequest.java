package com.elanor.cms.dto;

public class UpdateBannerRequest {

    private String title;
    private String subtitle;
    private String imageUrl;
    private String linkUrl;
    private String placement;
    private Integer displayOrder;
    private Boolean active;

    public UpdateBannerRequest() {}

    public UpdateBannerRequest(String title, String subtitle, String imageUrl, String linkUrl, String placement, Integer displayOrder, Boolean active) {
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
