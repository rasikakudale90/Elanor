package com.elanor.order.dto;

import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderDto {
    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingCharge;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String appliedCouponCode;
    private Integer estimatedDeliveryMin;
    private Integer estimatedDeliveryMax;
    private String notes;
    private List<OrderItemDto> items = new ArrayList<>();
    private OrderAddressDto address;
    private List<OrderStatusHistoryDto> statusHistory = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public OrderDto() {}

    public OrderDto(Order order) {
        if (order != null) {
            this.id = order.getId();
            this.orderNumber = order.getOrderNumber();
            if (order.getCustomer() != null) {
                this.customerId = order.getCustomer().getId();
                this.customerName = (order.getCustomer().getFirstName() + " " +
                        (order.getCustomer().getLastName() != null ? order.getCustomer().getLastName() : "")).trim();
                this.customerEmail = order.getCustomer().getUser() != null ? order.getCustomer().getUser().getEmail() : null;
            }
            this.status = order.getStatus();
            this.subtotal = order.getSubtotal();
            this.discountAmount = order.getDiscountAmount();
            this.shippingCharge = order.getShippingCharge();
            this.taxAmount = order.getTaxAmount();
            this.totalAmount = order.getTotalAmount();
            this.appliedCouponCode = order.getAppliedCouponCode();
            this.estimatedDeliveryMin = order.getEstimatedDeliveryMin();
            this.estimatedDeliveryMax = order.getEstimatedDeliveryMax();
            this.notes = order.getNotes();
            if (order.getItems() != null) {
                this.items = order.getItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
            }
            if (order.getAddress() != null) {
                this.address = new OrderAddressDto(order.getAddress());
            }
            if (order.getStatusHistory() != null) {
                this.statusHistory = order.getStatusHistory().stream().map(OrderStatusHistoryDto::new).collect(Collectors.toList());
            }
            this.createdAt = order.getCreatedAt();
            this.updatedAt = order.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(BigDecimal shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public void setAppliedCouponCode(String appliedCouponCode) {
        this.appliedCouponCode = appliedCouponCode;
    }

    public Integer getEstimatedDeliveryMin() {
        return estimatedDeliveryMin;
    }

    public void setEstimatedDeliveryMin(Integer estimatedDeliveryMin) {
        this.estimatedDeliveryMin = estimatedDeliveryMin;
    }

    public Integer getEstimatedDeliveryMax() {
        return estimatedDeliveryMax;
    }

    public void setEstimatedDeliveryMax(Integer estimatedDeliveryMax) {
        this.estimatedDeliveryMax = estimatedDeliveryMax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    public OrderAddressDto getAddress() {
        return address;
    }

    public void setAddress(OrderAddressDto address) {
        this.address = address;
    }

    public List<OrderStatusHistoryDto> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<OrderStatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
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
