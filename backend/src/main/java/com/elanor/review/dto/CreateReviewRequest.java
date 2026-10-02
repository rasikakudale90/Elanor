package com.elanor.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateReviewRequest {

    private String customerName;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;

    private String title;

    @NotBlank(message = "Review comment is required")
    private String comment;

    private List<String> mediaUrls;

    public CreateReviewRequest() {}

    public CreateReviewRequest(String customerName, int rating, String title, String comment, List<String> mediaUrls) {
        this.customerName = customerName;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.mediaUrls = mediaUrls;
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

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
