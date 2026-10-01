package com.elanor.payment.repository;

import com.elanor.order.entity.Order;
import com.elanor.payment.entity.Payment;
import com.elanor.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByOrderOrderByCreatedAtDesc(Order order);

    Optional<Payment> findFirstByOrderOrderByCreatedAtDesc(Order order);

    Optional<Payment> findByTransactionRef(String transactionRef);

    @Query("SELECT p FROM Payment p WHERE (:status IS NULL OR p.status = :status) " +
           "AND (:search IS NULL OR LOWER(p.transactionRef) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.order.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Payment> findAllWithFilter(@Param("status") PaymentStatus status,
                                    @Param("search") String search,
                                    Pageable pageable);
}
