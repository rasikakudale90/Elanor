package com.elanor.inventory.repository;

import com.elanor.inventory.entity.InventoryReservation;
import com.elanor.inventory.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
    List<InventoryReservation> findByOrderId(UUID orderId);
    Optional<InventoryReservation> findByIdAndStatus(UUID id, ReservationStatus status);
    List<InventoryReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant now);
}
