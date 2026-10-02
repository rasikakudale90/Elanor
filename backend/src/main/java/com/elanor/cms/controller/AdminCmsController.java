package com.elanor.cms.controller;

import com.elanor.cms.dto.BannerResponse;
import com.elanor.cms.dto.CreateBannerRequest;
import com.elanor.cms.dto.UpdateBannerRequest;
import com.elanor.cms.service.CmsService;
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
@RequestMapping("/api/v1/admin/cms")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminCmsController {

    private final CmsService cmsService;

    public AdminCmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getAllBanners(Pageable pageable) {
        Page<BannerResponse> response = cmsService.getAllBanners(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/banners")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody CreateBannerRequest request) {
        BannerResponse response = cmsService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Banner created successfully"));
    }

    @PutMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable UUID id,
            @RequestBody UpdateBannerRequest request) {
        BannerResponse response = cmsService.updateBanner(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Banner updated successfully"));
    }

    @DeleteMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable UUID id) {
        cmsService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.ok("Banner deleted successfully"));
    }
}
