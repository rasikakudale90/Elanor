package com.elanor.shipping.repository;

import com.elanor.shipping.entity.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, UUID> {
    List<ShipmentEvent> findByShipmentIdOrderByEventTimeAsc(UUID shipmentId);
}
