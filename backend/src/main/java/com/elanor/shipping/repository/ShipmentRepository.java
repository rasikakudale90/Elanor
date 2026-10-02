package com.elanor.shipping.repository;

import com.elanor.shipping.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    List<Shipment> findByOrderId(UUID orderId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    Page<Shipment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
