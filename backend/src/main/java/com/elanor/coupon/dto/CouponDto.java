package com.elanor.coupon.dto;

import com.elanor.coupon.entity.Coupon;
import com.elanor.coupon.entity.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CouponDto {
    private UUID id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private Instant startDate;
    private Instant endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    public CouponDto() {}

    public CouponDto(Coupon coupon) {
        if (coupon != null) {
            this.id = coupon.getId();
            this.code = coupon.getCode();
            this.description = coupon.getDescription();
            this.discountType = coupon.getDiscountType();
            this.discountValue = coupon.getDiscountValue();
            this.minOrderAmount = coupon.getMinOrderAmount();
            this.maxDiscountAmount = coupon.getMaxDiscountAmount();
            this.startDate = coupon.getStartDate();
            this.endDate = coupon.getEndDate();
            this.usageLimit = coupon.getUsageLimit();
            this.usageCount = coupon.getUsageCount();
            this.isActive = coupon.isActive();
            this.createdAt = coupon.getCreatedAt();
            this.updatedAt = coupon.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
