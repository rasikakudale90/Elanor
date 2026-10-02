package com.elanor.blog.dto;

import com.elanor.blog.entity.BlogPost;

import java.time.Instant;
import java.util.UUID;

public class BlogPostSummaryResponse {

    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private String author;
    private Instant publishedAt;

    public BlogPostSummaryResponse() {}

    public BlogPostSummaryResponse(BlogPost post) {
        if (post != null) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.slug = post.getSlug();
            this.summary = post.getSummary();
            this.coverImage = post.getCoverImage();
            this.author = post.getAuthor();
            this.publishedAt = post.getPublishedAt();
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

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
