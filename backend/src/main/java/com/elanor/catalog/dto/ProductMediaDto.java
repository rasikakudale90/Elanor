package com.elanor.catalog.dto;

import java.util.UUID;

public class ProductMediaDto {

    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String mediaUrl;
    private String mediaType;
    private int displayOrder;
    private boolean cover;

    public ProductMediaDto() {}

    public ProductMediaDto(UUID id, UUID productId, UUID variantId, String mediaUrl, String mediaType, int displayOrder, boolean cover) {
        this.id = id;
        this.productId = productId;
        this.variantId = variantId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.displayOrder = displayOrder;
        this.cover = cover;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
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
