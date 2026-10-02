package com.elanor.review.controller;

import com.elanor.common.response.ApiResponse;
import com.elanor.review.dto.ModerateReviewRequest;
import com.elanor.review.dto.ReviewResponse;
import com.elanor.review.enums.ReviewStatus;
import com.elanor.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @RequestParam(required = false) ReviewStatus status,
            Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getReviewsForAdmin(status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderateReview(
            @PathVariable UUID id,
            @Valid @RequestBody ModerateReviewRequest request) {
        ReviewResponse response = reviewService.moderateReview(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Review moderated successfully"));
    }
}
