package com.elanor.analytics.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AnalyticsOverviewResponse {

    private BigDecimal totalRevenue;
    private long totalOrders;
    private BigDecimal averageOrderValue;
    private long totalCustomers;
    private long totalEventsTracked;
    private Map<String, Long> ordersByStatus;

    public AnalyticsOverviewResponse() {}

    public AnalyticsOverviewResponse(BigDecimal totalRevenue, long totalOrders, BigDecimal averageOrderValue,
                                     long totalCustomers, long totalEventsTracked, Map<String, Long> ordersByStatus) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.averageOrderValue = averageOrderValue;
        this.totalCustomers = totalCustomers;
        this.totalEventsTracked = totalEventsTracked;
        this.ordersByStatus = ordersByStatus;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalEventsTracked() {
        return totalEventsTracked;
    }

    public void setTotalEventsTracked(long totalEventsTracked) {
        this.totalEventsTracked = totalEventsTracked;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }
}
