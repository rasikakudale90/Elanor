package com.elanor.order.entity;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PROCESSING,
    PACKED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    RETURNED,
    REFUNDED
}
