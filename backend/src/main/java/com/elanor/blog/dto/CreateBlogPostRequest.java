package com.elanor.blog.dto;

import com.elanor.blog.enums.BlogPostStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class CreateBlogPostRequest {

    @NotBlank(message = "Blog title is required")
    private String title;

    private String slug;

    private String summary;

    @NotBlank(message = "Blog content is required")
    private String content;

    private String coverImage;

    private String author;

    private BlogPostStatus status = BlogPostStatus.DRAFT;

    private Instant publishedAt;

    private String metaTitle;

    private String metaDescription;

    public CreateBlogPostRequest() {}

    public CreateBlogPostRequest(String title, String slug, String summary, String content,
                                 String coverImage, String author, BlogPostStatus status,
                                 Instant publishedAt, String metaTitle, String metaDescription) {
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.content = content;
        this.coverImage = coverImage;
        this.author = author;
        this.status = status;
        this.publishedAt = publishedAt;
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
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
}
