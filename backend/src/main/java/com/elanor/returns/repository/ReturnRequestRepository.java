package com.elanor.returns.repository;

import com.elanor.returns.entity.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {
    List<ReturnRequest> findByOrderId(UUID orderId);
    List<ReturnRequest> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    Page<ReturnRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
