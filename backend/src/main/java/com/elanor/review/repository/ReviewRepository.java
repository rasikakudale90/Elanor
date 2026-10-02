package com.elanor.review.repository;

import com.elanor.review.entity.Review;
import com.elanor.review.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductIdAndStatusOrderByCreatedAtDesc(UUID productId, ReviewStatus status, Pageable pageable);
    Page<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status, Pageable pageable);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
