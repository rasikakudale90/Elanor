package com.elanor.order.repository;

import com.elanor.customer.entity.CustomerProfile;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerOrderByCreatedAtDesc(CustomerProfile customer, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status) " +
           "AND (:search IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR (o.customer IS NOT NULL AND (LOWER(o.customer.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.customer.lastName) LIKE LOWER(CONCAT('%', :search, '%'))))) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findAllWithFilter(@Param("status") OrderStatus status,
                                  @Param("search") String search,
                                  Pageable pageable);
}
