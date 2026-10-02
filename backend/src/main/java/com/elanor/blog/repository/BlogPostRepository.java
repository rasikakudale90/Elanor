package com.elanor.blog.repository;

import com.elanor.blog.entity.BlogPost;
import com.elanor.blog.enums.BlogPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    Optional<BlogPost> findBySlug(String slug);
    Optional<BlogPost> findBySlugAndStatus(String slug, BlogPostStatus status);
    Page<BlogPost> findByStatusOrderByPublishedAtDesc(BlogPostStatus status, Pageable pageable);
    Page<BlogPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
