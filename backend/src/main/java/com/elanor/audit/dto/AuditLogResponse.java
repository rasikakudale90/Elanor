package com.elanor.audit.dto;

import com.elanor.audit.entity.AuditLog;

import java.time.Instant;
import java.util.UUID;

public class AuditLogResponse {

    private UUID id;
    private String actor;
    private String action;
    private String entityType;
    private String entityId;
    private String detailsJson;
    private String ipAddress;
    private Instant createdAt;

    public AuditLogResponse() {}

    public AuditLogResponse(AuditLog log) {
        if (log != null) {
            this.id = log.getId();
            this.actor = log.getActor();
            this.action = log.getAction();
            this.entityType = log.getEntityType();
            this.entityId = log.getEntityId();
            this.detailsJson = log.getDetailsJson();
            this.ipAddress = log.getIpAddress();
            this.createdAt = log.getCreatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
