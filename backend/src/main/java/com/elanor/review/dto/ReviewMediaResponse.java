package com.elanor.review.dto;

import com.elanor.review.entity.ReviewMedia;

import java.time.Instant;
import java.util.UUID;

public class ReviewMediaResponse {

    private UUID id;
    private String mediaUrl;
    private String mediaType;
    private Instant createdAt;

    public ReviewMediaResponse() {}

    public ReviewMediaResponse(ReviewMedia media) {
        if (media != null) {
            this.id = media.getId();
            this.mediaUrl = media.getMediaUrl();
            this.mediaType = media.getMediaType();
            this.createdAt = media.getCreatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
