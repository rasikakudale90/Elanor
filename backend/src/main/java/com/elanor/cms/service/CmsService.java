package com.elanor.cms.service;

import com.elanor.cms.dto.BannerResponse;
import com.elanor.cms.dto.CreateBannerRequest;
import com.elanor.cms.dto.UpdateBannerRequest;
import com.elanor.cms.entity.Banner;
import com.elanor.cms.repository.BannerRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CmsService {

    private static final Logger log = LoggerFactory.getLogger(CmsService.class);

    private final BannerRepository bannerRepository;

    public CmsService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners(String placement) {
        List<Banner> banners;
        if (placement != null && !placement.isBlank()) {
            banners = bannerRepository.findByPlacementAndIsActiveOrderByDisplayOrderAsc(placement, true);
        } else {
            banners = bannerRepository.findByIsActiveOrderByDisplayOrderAsc(true);
        }
        return banners.stream().map(BannerResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public BannerResponse createBanner(CreateBannerRequest request) {
        Banner banner = new Banner(
                request.getTitle(),
                request.getSubtitle(),
                request.getImageUrl(),
                request.getLinkUrl(),
                request.getPlacement(),
                request.getDisplayOrder(),
                request.isActive()
        );
        Banner saved = bannerRepository.save(banner);
        log.info("Created CMS banner [{}] with placement [{}]", saved.getId(), saved.getPlacement());
        return new BannerResponse(saved);
    }

    @Transactional
    public BannerResponse updateBanner(UUID id, UpdateBannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Banner not found with ID: " + id));

        if (request.getTitle() != null) banner.setTitle(request.getTitle());
        if (request.getSubtitle() != null) banner.setSubtitle(request.getSubtitle());
        if (request.getImageUrl() != null) banner.setImageUrl(request.getImageUrl());
        if (request.getLinkUrl() != null) banner.setLinkUrl(request.getLinkUrl());
        if (request.getPlacement() != null) banner.setPlacement(request.getPlacement());
        if (request.getDisplayOrder() != null) banner.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) banner.setActive(request.getActive());

        Banner saved = bannerRepository.save(banner);
        return new BannerResponse(saved);
    }

    @Transactional
    public void deleteBanner(UUID id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Banner not found with ID: " + id));
        bannerRepository.delete(banner);
        log.info("Deleted CMS banner [{}]", id);
    }

    @Transactional(readOnly = true)
    public Page<BannerResponse> getAllBanners(Pageable pageable) {
        return bannerRepository.findAllByOrderByDisplayOrderAsc(pageable).map(BannerResponse::new);
    }
}
