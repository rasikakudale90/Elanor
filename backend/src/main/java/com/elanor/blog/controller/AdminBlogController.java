package com.elanor.blog.controller;

import com.elanor.blog.dto.BlogPostResponse;
import com.elanor.blog.dto.CreateBlogPostRequest;
import com.elanor.blog.dto.UpdateBlogPostRequest;
import com.elanor.blog.service.BlogService;
import com.elanor.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/blog")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<BlogPostResponse>>> getAllPosts(Pageable pageable) {
        Page<BlogPostResponse> response = blogService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getPostById(@PathVariable UUID id) {
        BlogPostResponse response = blogService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<BlogPostResponse>> createPost(@Valid @RequestBody CreateBlogPostRequest request) {
        BlogPostResponse response = blogService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Blog post created successfully"));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> updatePost(
            @PathVariable UUID id,
            @RequestBody UpdateBlogPostRequest request) {
        BlogPostResponse response = blogService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Blog post updated successfully"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable UUID id) {
        blogService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.ok("Blog post deleted successfully"));
    }
}
