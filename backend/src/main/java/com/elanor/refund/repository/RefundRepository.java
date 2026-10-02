package com.elanor.refund.repository;

import com.elanor.refund.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {
    List<Refund> findByOrderId(UUID orderId);
    Page<Refund> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
