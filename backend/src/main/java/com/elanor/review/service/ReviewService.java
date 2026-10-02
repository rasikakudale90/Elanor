package com.elanor.review.service;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.review.dto.CreateReviewRequest;
import com.elanor.review.dto.ModerateReviewRequest;
import com.elanor.review.dto.ReviewResponse;
import com.elanor.review.entity.Review;
import com.elanor.review.entity.ReviewMedia;
import com.elanor.review.enums.ReviewStatus;
import com.elanor.review.repository.ReviewMediaRepository;
import com.elanor.review.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    private final ProductRepository productRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ReviewMediaRepository reviewMediaRepository,
                         ProductRepository productRepository,
                         CustomerProfileRepository customerProfileRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMediaRepository = reviewMediaRepository;
        this.productRepository = productRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    @Transactional
    public ReviewResponse submitReview(UUID productId, UUID userId, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found with ID: " + productId));

        CustomerProfile customer = null;
        String reviewerName = request.getCustomerName() != null ? request.getCustomerName().trim() : "Élanor Guest";

        if (userId != null) {
            customer = customerProfileRepository.findByUserId(userId).orElse(null);
            if (customer != null) {
                reviewerName = customer.getFirstName() + (customer.getLastName() != null ? " " + customer.getLastName() : "");
            }
        }

        Review review = new Review(
                product,
                customer,
                reviewerName,
                request.getRating(),
                request.getTitle(),
                request.getComment()
        );

        if (request.getMediaUrls() != null) {
            for (String url : request.getMediaUrls()) {
                ReviewMedia media = new ReviewMedia(review, url, "IMAGE");
                review.addMedia(media);
            }
        }

        Review saved = reviewRepository.save(review);
        log.info("Submitted review [{}] for product [{}] by [{}] with initial status [PENDING]",
                saved.getId(), product.getName(), reviewerName);

        return new ReviewResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductApprovedReviews(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(productId, ReviewStatus.APPROVED, pageable)
                .map(ReviewResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForAdmin(ReviewStatus status, Pageable pageable) {
        if (status != null) {
            return reviewRepository.findByStatusOrderByCreatedAtDesc(status, pageable).map(ReviewResponse::new);
        }
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable).map(ReviewResponse::new);
    }

    @Transactional
    public ReviewResponse moderateReview(UUID reviewId, ModerateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Review not found with ID: " + reviewId));

        review.setStatus(request.getStatus());
        Review saved = reviewRepository.save(review);

        log.info("Moderated review [{}] to status [{}]", reviewId, request.getStatus());
        return new ReviewResponse(saved);
    }
}
