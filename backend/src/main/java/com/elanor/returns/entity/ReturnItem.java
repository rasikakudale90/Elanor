package com.elanor.returns.entity;

import com.elanor.order.entity.OrderItem;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "return_items")
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private int quantity = 1;

    @Column(name = "condition_note", length = 255)
    private String conditionNote;

    public ReturnItem() {}

    public ReturnItem(ReturnRequest returnRequest, OrderItem orderItem, int quantity, String conditionNote) {
        this.returnRequest = returnRequest;
        this.orderItem = orderItem;
        this.quantity = quantity;
        this.conditionNote = conditionNote;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
    }

    public void setReturnRequest(ReturnRequest returnRequest) {
        this.returnRequest = returnRequest;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
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
