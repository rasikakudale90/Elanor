package com.elanor.returns.dto;

import com.elanor.returns.entity.ReturnRequest;
import com.elanor.returns.enums.ReturnStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReturnRequestResponse {

    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private String customerName;
    private ReturnStatus status;
    private String reason;
    private String comments;
    private boolean isReplacement;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ReturnItemResponse> items = new ArrayList<>();

    public ReturnRequestResponse() {}

    public ReturnRequestResponse(ReturnRequest req) {
        if (req != null) {
            this.id = req.getId();
            if (req.getOrder() != null) {
                this.orderId = req.getOrder().getId();
                this.orderNumber = req.getOrder().getOrderNumber();
            }
            if (req.getCustomer() != null) {
                this.customerId = req.getCustomer().getId();
                this.customerName = req.getCustomer().getFirstName() + (req.getCustomer().getLastName() != null ? " " + req.getCustomer().getLastName() : "");
            }
            this.status = req.getStatus();
            this.reason = req.getReason();
            this.comments = req.getComments();
            this.isReplacement = req.isReplacement();
            this.createdAt = req.getCreatedAt();
            this.updatedAt = req.getUpdatedAt();
            if (req.getItems() != null) {
                this.items = req.getItems().stream()
                        .map(ReturnItemResponse::new)
                        .collect(Collectors.toList());
            }
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

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isReplacement() {
        return isReplacement;
    }

    public void setReplacement(boolean replacement) {
        isReplacement = replacement;
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

    public List<ReturnItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ReturnItemResponse> items) {
        this.items = items;
    }
}
