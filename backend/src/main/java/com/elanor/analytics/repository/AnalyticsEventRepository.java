package com.elanor.analytics.repository;

import com.elanor.analytics.entity.AnalyticsEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, UUID> {
    Page<AnalyticsEvent> findByEventTypeOrderByCreatedAtDesc(String eventType, Pageable pageable);
    long countByEventType(String eventType);
    long countByCreatedAtAfter(Instant after);
}
