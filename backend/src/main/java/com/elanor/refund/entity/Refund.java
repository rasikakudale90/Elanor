package com.elanor.refund.entity;

import com.elanor.order.entity.Order;
import com.elanor.refund.enums.RefundMethod;
import com.elanor.refund.enums.RefundStatus;
import com.elanor.returns.entity.ReturnRequest;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refunds")
@EntityListeners(AuditingEntityListener.class)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id")
    private ReturnRequest returnRequest;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RefundStatus status = RefundStatus.REFUND_PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_method", nullable = false, length = 50)
    private RefundMethod refundMethod = RefundMethod.ORIGINAL_SOURCE;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Refund() {}

    public Refund(Order order, ReturnRequest returnRequest, BigDecimal amount,
                  RefundStatus status, RefundMethod refundMethod, String referenceId, String processedBy) {
        this.order = order;
        this.returnRequest = returnRequest;
        this.amount = amount;
        this.status = status != null ? status : RefundStatus.REFUND_PENDING;
        this.refundMethod = refundMethod != null ? refundMethod : RefundMethod.ORIGINAL_SOURCE;
        this.referenceId = referenceId;
        this.processedBy = processedBy;
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

    public ReturnRequest getReturnRequest() {
        return returnRequest;
    }

    public void setReturnRequest(ReturnRequest returnRequest) {
        this.returnRequest = returnRequest;
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
