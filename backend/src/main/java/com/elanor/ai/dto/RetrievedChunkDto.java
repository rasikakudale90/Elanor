package com.elanor.ai.dto;

import java.util.UUID;

public class RetrievedChunkDto {

    private UUID id;
    private String sourceType;
    private String sourceId;
    private String title;
    private String content;
    private double score;
    private String metadataJson;

    public RetrievedChunkDto() {}

    public RetrievedChunkDto(UUID id, String sourceType, String sourceId, String title, String content, double score, String metadataJson) {
        this.id = id;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.title = title;
        this.content = content;
        this.score = score;
        this.metadataJson = metadataJson;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
