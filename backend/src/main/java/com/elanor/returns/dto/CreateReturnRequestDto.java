package com.elanor.returns.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class CreateReturnRequestDto {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotBlank(message = "Return reason is required")
    private String reason;

    private String comments;

    private boolean isReplacement = false;

    @NotEmpty(message = "At least one item must be selected for return")
    @Valid
    private List<CreateReturnItemDto> items;

    public CreateReturnRequestDto() {}

    public CreateReturnRequestDto(UUID orderId, String reason, String comments, boolean isReplacement, List<CreateReturnItemDto> items) {
        this.orderId = orderId;
        this.reason = reason;
        this.comments = comments;
        this.isReplacement = isReplacement;
        this.items = items;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
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

    public List<CreateReturnItemDto> getItems() {
        return items;
    }

    public void setItems(List<CreateReturnItemDto> items) {
        this.items = items;
    }
}
