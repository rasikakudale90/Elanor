package com.elanor.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRecommendationDto {

    private UUID productId;
    private UUID variantId;
    private String name;
    private String subtitle;
    private BigDecimal price;
    private String imageUrl;
    private String rationale;

    public ProductRecommendationDto() {}

    public ProductRecommendationDto(UUID productId, UUID variantId, String name, String subtitle, BigDecimal price, String imageUrl, String rationale) {
        this.productId = productId;
        this.variantId = variantId;
        this.name = name;
        this.subtitle = subtitle;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rationale = rationale;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}
