package com.elanor.checkout.dto;

import com.elanor.order.dto.OrderDto;

public class CheckoutResponse {

    private OrderDto order;
    private boolean paymentRequired;
    private String paymentMethod;
    private String message;

    public CheckoutResponse() {}

    public CheckoutResponse(OrderDto order, boolean paymentRequired, String paymentMethod, String message) {
        this.order = order;
        this.paymentRequired = paymentRequired;
        this.paymentMethod = paymentMethod;
        this.message = message;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public void setPaymentRequired(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
