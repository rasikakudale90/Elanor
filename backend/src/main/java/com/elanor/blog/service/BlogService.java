package com.elanor.blog.service;

import com.elanor.blog.dto.BlogPostResponse;
import com.elanor.blog.dto.BlogPostSummaryResponse;
import com.elanor.blog.dto.CreateBlogPostRequest;
import com.elanor.blog.dto.UpdateBlogPostRequest;
import com.elanor.blog.entity.BlogPost;
import com.elanor.blog.enums.BlogPostStatus;
import com.elanor.blog.repository.BlogPostRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class BlogService {

    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    private final BlogPostRepository blogPostRepository;

    public BlogService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> getPublishedPosts(Pageable pageable) {
        return blogPostRepository.findByStatusOrderByPublishedAtDesc(BlogPostStatus.PUBLISHED, pageable)
                .map(BlogPostSummaryResponse::new);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getPostBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlugAndStatus(slug, BlogPostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Blog article not found for slug: " + slug));
        return new BlogPostResponse(post);
    }

    @Transactional
    public BlogPostResponse createPost(CreateBlogPostRequest request) {
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = request.getTitle().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        if (blogPostRepository.findBySlug(slug).isPresent()) {
            throw new BusinessException(ErrorCode.CONFLICT, "A blog post with this slug already exists: " + slug);
        }

        BlogPost post = new BlogPost(
                request.getTitle(),
                slug,
                request.getSummary(),
                request.getContent(),
                request.getCoverImage(),
                request.getAuthor(),
                request.getStatus(),
                request.getPublishedAt(),
                request.getMetaTitle(),
                request.getMetaDescription()
        );

        BlogPost saved = blogPostRepository.save(post);
        log.info("Created blog post [{}] with slug [{}]", saved.getId(), saved.getSlug());
        return new BlogPostResponse(saved);
    }

    @Transactional
    public BlogPostResponse updatePost(UUID id, UpdateBlogPostRequest request) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post not found with ID: " + id));

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getSlug() != null && !request.getSlug().equals(post.getSlug())) {
            if (blogPostRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new BusinessException(ErrorCode.CONFLICT, "A blog post with this slug already exists: " + request.getSlug());
            }
            post.setSlug(request.getSlug());
        }
        if (request.getSummary() != null) post.setSummary(request.getSummary());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getCoverImage() != null) post.setCoverImage(request.getCoverImage());
        if (request.getAuthor() != null) post.setAuthor(request.getAuthor());
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
            if (request.getStatus() == BlogPostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(Instant.now());
            }
        }
        if (request.getPublishedAt() != null) post.setPublishedAt(request.getPublishedAt());
        if (request.getMetaTitle() != null) post.setMetaTitle(request.getMetaTitle());
        if (request.getMetaDescription() != null) post.setMetaDescription(request.getMetaDescription());

        BlogPost saved = blogPostRepository.save(post);
        return new BlogPostResponse(saved);
    }

    @Transactional
    public void deletePost(UUID id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post not found with ID: " + id));
        blogPostRepository.delete(post);
        log.info("Deleted blog post [{}]", id);
    }

    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getAllPosts(Pageable pageable) {
        return blogPostRepository.findAllByOrderByCreatedAtDesc(pageable).map(BlogPostResponse::new);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getPostById(UUID id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post not found with ID: " + id));
        return new BlogPostResponse(post);
    }
}
