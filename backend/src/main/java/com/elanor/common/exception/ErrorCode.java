package com.elanor.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Auth & Identity
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password."),
    AUTH_EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Email is not verified. Please verify your email first."),
    AUTH_OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one."),
    AUTH_OTP_INVALID(HttpStatus.BAD_REQUEST, "Invalid OTP provided."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token has expired."),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required to access this resource."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission to perform this action."),

    // Catalog & Products
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found."),
    PRODUCT_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "The requested product is not active."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "The selected product is currently out of stock."),
    VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product variant not found."),

    // Cart & Wishlist
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart item not found."),
    CART_STOCK_CHANGED(HttpStatus.CONFLICT, "Item stock availability has changed."),
    CART_PRICE_CHANGED(HttpStatus.CONFLICT, "Item price has been updated."),

    // Coupons
    COUPON_INVALID(HttpStatus.BAD_REQUEST, "Invalid coupon code."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "This coupon has expired."),
    COUPON_NOT_ELIGIBLE(HttpStatus.BAD_REQUEST, "Order does not meet the eligibility requirements for this coupon."),
    COUPON_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "Only one coupon can be applied per order."),

    // Checkout & Orders
    CHECKOUT_INVALID(HttpStatus.BAD_REQUEST, "Checkout validation failed."),
    ADDRESS_INVALID(HttpStatus.BAD_REQUEST, "Invalid or incomplete address provided."),
    SHIPPING_UNAVAILABLE(HttpStatus.BAD_REQUEST, "Shipping is currently unavailable for this address."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "Order cannot be cancelled in its current state."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "Order has already been cancelled."),

    // Payments
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment processing failed."),
    PAYMENT_PENDING(HttpStatus.ACCEPTED, "Payment is still pending."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "Payment for this order has already been processed."),
    PAYMENT_INVALID_STATE(HttpStatus.BAD_REQUEST, "Invalid payment transition state."),

    // Returns & Refunds
    RETURN_NOT_ELIGIBLE(HttpStatus.BAD_REQUEST, "This item is not eligible for return."),
    RETURN_WINDOW_EXPIRED(HttpStatus.BAD_REQUEST, "The 7-day return window for this order has expired."),
    RETURN_ALREADY_REQUESTED(HttpStatus.CONFLICT, "A return request has already been submitted for this item."),
    REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "Requested refund amount exceeds refundable limit."),
    REFUND_INVALID_STATE(HttpStatus.BAD_REQUEST, "Invalid refund state."),

    // Common & System
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource not found."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Input validation failed."),
    CONFLICT(HttpStatus.CONFLICT, "A resource conflict occurred."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
