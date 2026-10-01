package com.elanor.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class CreateMediaRequest {

    private UUID variantId;

    @NotBlank(message = "Media URL is required")
    private String mediaUrl;

    private String mediaType = "IMAGE"; // IMAGE, VIDEO
    private int displayOrder = 0;
    private boolean cover = false;

    public CreateMediaRequest() {}

    public CreateMediaRequest(UUID variantId, String mediaUrl, String mediaType, int displayOrder, boolean cover) {
        this.variantId = variantId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType != null ? mediaType : "IMAGE";
        this.displayOrder = displayOrder;
        this.cover = cover;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isCover() {
        return cover;
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }
}
