package com.elanor.order.repository;

import com.elanor.order.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, UUID> {
    Optional<OrderAddress> findByOrderId(UUID orderId);
}
