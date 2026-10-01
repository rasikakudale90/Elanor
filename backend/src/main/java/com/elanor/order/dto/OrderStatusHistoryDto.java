package com.elanor.order.dto;

import com.elanor.order.entity.OrderStatus;
import com.elanor.order.entity.OrderStatusHistory;

import java.time.Instant;
import java.util.UUID;

public class OrderStatusHistoryDto {
    private UUID id;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private String note;
    private String actor;
    private Instant createdAt;

    public OrderStatusHistoryDto() {}

    public OrderStatusHistoryDto(OrderStatusHistory history) {
        if (history != null) {
            this.id = history.getId();
            this.fromStatus = history.getFromStatus();
            this.toStatus = history.getToStatus();
            this.note = history.getNote();
            this.actor = history.getActor();
            this.createdAt = history.getCreatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(OrderStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public OrderStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(OrderStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
