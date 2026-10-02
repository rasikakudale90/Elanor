package com.elanor.blog.controller;

import com.elanor.blog.dto.BlogPostResponse;
import com.elanor.blog.dto.BlogPostSummaryResponse;
import com.elanor.blog.service.BlogService;
import com.elanor.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<BlogPostSummaryResponse>>> getPublishedPosts(Pageable pageable) {
        Page<BlogPostSummaryResponse> response = blogService.getPublishedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getPostBySlug(@PathVariable String slug) {
        BlogPostResponse response = blogService.getPostBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
