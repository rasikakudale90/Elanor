package com.elanor.blog.dto;

import com.elanor.blog.entity.BlogPost;
import com.elanor.blog.enums.BlogPostStatus;

import java.time.Instant;
import java.util.UUID;

public class BlogPostResponse {

    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private BlogPostStatus status;
    private Instant publishedAt;
    private String metaTitle;
    private String metaDescription;
    private Instant createdAt;
    private Instant updatedAt;

    public BlogPostResponse() {}

    public BlogPostResponse(BlogPost post) {
        if (post != null) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.slug = post.getSlug();
            this.summary = post.getSummary();
            this.content = post.getContent();
            this.coverImage = post.getCoverImage();
            this.author = post.getAuthor();
            this.status = post.getStatus();
            this.publishedAt = post.getPublishedAt();
            this.metaTitle = post.getMetaTitle();
            this.metaDescription = post.getMetaDescription();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BlogPostStatus getStatus() {
        return status;
    }

    public void setStatus(BlogPostStatus status) {
        this.status = status;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
