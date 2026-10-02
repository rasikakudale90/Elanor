package com.elanor.audit.service;

import com.elanor.audit.dto.AuditLogResponse;
import com.elanor.audit.entity.AuditLog;
import com.elanor.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(String actor, String action, String entityType, String entityId, String detailsJson, String ipAddress) {
        AuditLog auditLog = new AuditLog(actor, action, entityType, entityId, detailsJson, ipAddress);
        auditLogRepository.save(auditLog);
        log.info("[AUDIT] Actor [{}] performed [{}] on [{}:{}]", actor, action, entityType, entityId);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String entityType, String actor, Pageable pageable) {
        if (entityType != null && !entityType.isBlank()) {
            return auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType, pageable).map(AuditLogResponse::new);
        }
        if (actor != null && !actor.isBlank()) {
            return auditLogRepository.findByActorOrderByCreatedAtDesc(actor, pageable).map(AuditLogResponse::new);
        }
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(AuditLogResponse::new);
    }
}
