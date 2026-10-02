package com.elanor.returns.dto;

import com.elanor.returns.entity.ReturnItem;

import java.math.BigDecimal;
import java.util.UUID;

public class ReturnItemResponse {

    private UUID id;
    private UUID orderItemId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
    private String conditionNote;

    public ReturnItemResponse() {}

    public ReturnItemResponse(ReturnItem item) {
        if (item != null) {
            this.id = item.getId();
            if (item.getOrderItem() != null) {
                this.orderItemId = item.getOrderItem().getId();
                this.productName = item.getOrderItem().getProductName();
                this.sku = item.getOrderItem().getSku();
                this.unitPrice = item.getOrderItem().getUnitPrice();
            }
            this.quantity = item.getQuantity();
            this.conditionNote = item.getConditionNote();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }
}
