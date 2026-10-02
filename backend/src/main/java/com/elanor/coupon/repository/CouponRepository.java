package com.elanor.coupon.repository;

import com.elanor.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now AND " +
           "(c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
    List<Coupon> findActiveCoupons(@Param("now") Instant now);

    @Query("SELECT c FROM Coupon c WHERE (:search IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Coupon> findAllWithFilter(@Param("search") String search, Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Coupon c SET c.usageCount = c.usageCount + 1 WHERE UPPER(c.code) = UPPER(:code)")
    int incrementUsageCount(@Param("code") String code);
}
