package com.elanor.review.dto;

import com.elanor.review.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;

public class ModerateReviewRequest {

    @NotNull(message = "Review status is required")
    private ReviewStatus status;

    public ModerateReviewRequest() {}

    public ModerateReviewRequest(ReviewStatus status) {
        this.status = status;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }
}
