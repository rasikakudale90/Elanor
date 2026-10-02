package com.elanor.review.dto;

import com.elanor.review.entity.Review;
import com.elanor.review.enums.ReviewStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReviewResponse {

    private UUID id;
    private UUID productId;
    private String productName;
    private UUID customerId;
    private String customerName;
    private int rating;
    private String title;
    private String comment;
    private ReviewStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ReviewMediaResponse> media = new ArrayList<>();

    public ReviewResponse() {}

    public ReviewResponse(Review review) {
        if (review != null) {
            this.id = review.getId();
            if (review.getProduct() != null) {
                this.productId = review.getProduct().getId();
                this.productName = review.getProduct().getName();
            }
            if (review.getCustomer() != null) {
                this.customerId = review.getCustomer().getId();
            }
            this.customerName = review.getCustomerName();
            this.rating = review.getRating();
            this.title = review.getTitle();
            this.comment = review.getComment();
            this.status = review.getStatus();
            this.createdAt = review.getCreatedAt();
            this.updatedAt = review.getUpdatedAt();
            if (review.getMedia() != null) {
                this.media = review.getMedia().stream()
                        .map(ReviewMediaResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
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

    public List<ReviewMediaResponse> getMedia() {
        return media;
    }

    public void setMedia(List<ReviewMediaResponse> media) {
        this.media = media;
    }
}
