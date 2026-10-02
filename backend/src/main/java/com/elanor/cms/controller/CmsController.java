package com.elanor.cms.controller;

import com.elanor.cms.dto.BannerResponse;
import com.elanor.cms.service.CmsService;
import com.elanor.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
public class CmsController {

    private final CmsService cmsService;

    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(@RequestParam(required = false) String placement) {
        List<BannerResponse> response = cmsService.getActiveBanners(placement);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
