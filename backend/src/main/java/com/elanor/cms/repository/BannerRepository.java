package com.elanor.cms.repository;

import com.elanor.cms.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {
    List<Banner> findByPlacementAndIsActiveOrderByDisplayOrderAsc(String placement, boolean isActive);
    List<Banner> findByIsActiveOrderByDisplayOrderAsc(boolean isActive);
    Page<Banner> findAllByOrderByDisplayOrderAsc(Pageable pageable);
}
