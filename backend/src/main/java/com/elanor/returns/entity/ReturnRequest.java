package com.elanor.returns.entity;

import com.elanor.customer.entity.CustomerProfile;
import com.elanor.order.entity.Order;
import com.elanor.returns.enums.ReturnStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "return_requests")
@EntityListeners(AuditingEntityListener.class)
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReturnStatus status = ReturnStatus.REQUESTED;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "is_replacement", nullable = false)
    private boolean isReplacement = false;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> items = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public ReturnRequest() {}

    public ReturnRequest(Order order, CustomerProfile customer, String reason, String comments, boolean isReplacement) {
        this.order = order;
        this.customer = customer;
        this.reason = reason;
        this.comments = comments;
        this.isReplacement = isReplacement;
        this.status = ReturnStatus.REQUESTED;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CustomerProfile getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerProfile customer) {
        this.customer = customer;
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

    public List<ReturnItem> getItems() {
        return items;
    }

    public void setItems(List<ReturnItem> items) {
        this.items = items;
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

    public void addItem(ReturnItem item) {
        items.add(item);
        item.setReturnRequest(this);
    }
}
