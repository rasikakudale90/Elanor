package com.elanor.refund.dto;

import com.elanor.refund.enums.RefundMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ProcessRefundRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private UUID returnRequestId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Refund method is required")
    private RefundMethod refundMethod = RefundMethod.ORIGINAL_SOURCE;

    private String referenceId;

    private String processedBy;

    public ProcessRefundRequest() {}

    public ProcessRefundRequest(UUID orderId, UUID returnRequestId, BigDecimal amount,
                                RefundMethod refundMethod, String referenceId, String processedBy) {
        this.orderId = orderId;
        this.returnRequestId = returnRequestId;
        this.amount = amount;
        this.refundMethod = refundMethod;
        this.referenceId = referenceId;
        this.processedBy = processedBy;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
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
}
