package com.elanor.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class RitualStepDto {

    private int stepNumber;
    private String stepName; // e.g. "Step 1: Cleanse & Purify", "Step 2: Deep Cellular Activation"
    private String timing; // "MORNING", "EVENING", "BOTH"
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String productSubtitle;
    private BigDecimal price;
    private String imageUrl;
    private String applicationTechnique;
    private String formulationReason;

    public RitualStepDto() {}

    public RitualStepDto(int stepNumber, String stepName, String timing, UUID productId, UUID variantId, String productName, String productSubtitle, BigDecimal price, String imageUrl, String applicationTechnique, String formulationReason) {
        this.stepNumber = stepNumber;
        this.stepName = stepName;
        this.timing = timing;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.price = price;
        this.imageUrl = imageUrl;
        this.applicationTechnique = applicationTechnique;
        this.formulationReason = formulationReason;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
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

    public String getApplicationTechnique() {
        return applicationTechnique;
    }

    public void setApplicationTechnique(String applicationTechnique) {
        this.applicationTechnique = applicationTechnique;
    }

    public String getFormulationReason() {
        return formulationReason;
    }

    public void setFormulationReason(String formulationReason) {
        this.formulationReason = formulationReason;
    }
}
