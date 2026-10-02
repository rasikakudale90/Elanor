package com.elanor.audit.controller;

import com.elanor.audit.dto.AuditLogResponse;
import com.elanor.audit.service.AuditLogService;
import com.elanor.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAuditController {

    private final AuditLogService auditLogService;

    public AdminAuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actor,
            Pageable pageable) {
        Page<AuditLogResponse> response = auditLogService.getAuditLogs(entityType, actor, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
