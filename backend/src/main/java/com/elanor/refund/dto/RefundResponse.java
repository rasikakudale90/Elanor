package com.elanor.refund.dto;

import com.elanor.refund.entity.Refund;
import com.elanor.refund.enums.RefundMethod;
import com.elanor.refund.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RefundResponse {

    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private UUID returnRequestId;
    private BigDecimal amount;
    private RefundStatus status;
    private RefundMethod refundMethod;
    private String referenceId;
    private String processedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public RefundResponse() {}

    public RefundResponse(Refund refund) {
        if (refund != null) {
            this.id = refund.getId();
            if (refund.getOrder() != null) {
                this.orderId = refund.getOrder().getId();
                this.orderNumber = refund.getOrder().getOrderNumber();
            }
            if (refund.getReturnRequest() != null) {
                this.returnRequestId = refund.getReturnRequest().getId();
            }
            this.amount = refund.getAmount();
            this.status = refund.getStatus();
            this.refundMethod = refund.getRefundMethod();
            this.referenceId = refund.getReferenceId();
            this.processedBy = refund.getProcessedBy();
            this.createdAt = refund.getCreatedAt();
            this.updatedAt = refund.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public UUID getReturnRequestId() {
        return returnRequestId;
    }

    public void setReturnRequestId(UUID returnRequestId) {
        this.returnRequestId = returnRequestId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public RefundMethod getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(RefundMethod refundMethod) {
        this.refundMethod = refundMethod;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
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
