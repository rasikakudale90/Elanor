package com.elanor.review.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.common.security.UserPrincipal;
import com.elanor.review.dto.CreateReviewRequest;
import com.elanor.review.dto.ReviewResponse;
import com.elanor.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        ReviewResponse response = reviewService.submitReview(productId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Review submitted and pending moderation"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getProductApprovedReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
