package com.elanor.wishlist.service;

import com.elanor.catalog.entity.ProductMedia;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.inventory.entity.Inventory;
import com.elanor.inventory.service.InventoryService;
import com.elanor.wishlist.dto.*;
import com.elanor.wishlist.entity.Wishlist;
import com.elanor.wishlist.entity.WishlistItem;
import com.elanor.wishlist.repository.WishlistItemRepository;
import com.elanor.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryService inventoryService;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            CustomerProfileRepository customerProfileRepository,
            ProductVariantRepository productVariantRepository,
            InventoryService inventoryService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Wishlist getOrCreateWishlist(UUID userId, String guestToken) {
        if (userId != null) {
            CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

            return wishlistRepository.findByCustomer(customer)
                    .orElseGet(() -> wishlistRepository.save(new Wishlist(customer)));
        } else if (guestToken != null && !guestToken.isBlank()) {
            return wishlistRepository.findByGuestToken(guestToken)
                    .orElseGet(() -> wishlistRepository.save(new Wishlist(guestToken)));
        } else {
            String newToken = UUID.randomUUID().toString();
            return wishlistRepository.save(new Wishlist(newToken));
        }
    }

    @Transactional(readOnly = true)
    public WishlistDto getWishlistDto(UUID userId, String guestToken) {
        Wishlist wishlist = getOrCreateWishlist(userId, guestToken);
        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto addItem(UUID userId, String guestToken, AddWishlistItemRequest request) {
        Wishlist wishlist = getOrCreateWishlist(userId, guestToken);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        if (!variant.isActive() || (variant.getProduct() != null && !variant.getProduct().isCurrentlyPublishable())) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ACTIVE);
        }

        Optional<WishlistItem> existing = wishlistItemRepository.findByWishlistAndVariant(wishlist, variant);
        if (existing.isEmpty()) {
            WishlistItem newItem = new WishlistItem(wishlist, variant);
            wishlistItemRepository.save(newItem);
            wishlist.getItems().add(newItem);
        }

        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto removeItem(UUID userId, String guestToken, UUID variantId) {
        Wishlist wishlist = getOrCreateWishlist(userId, guestToken);
        wishlistItemRepository.deleteByWishlistAndVariantId(wishlist, variantId);
        wishlist.getItems().removeIf(item -> item.getVariant().getId().equals(variantId));
        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto mergeWishlist(UUID userId, String guestToken) {
        if (guestToken == null || guestToken.isBlank()) {
            return getWishlistDto(userId, null);
        }

        CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        Wishlist customerWishlist = wishlistRepository.findByCustomer(customer)
                .orElseGet(() -> wishlistRepository.save(new Wishlist(customer)));

        Optional<Wishlist> guestWishlistOpt = wishlistRepository.findByGuestToken(guestToken);
        if (guestWishlistOpt.isPresent()) {
            Wishlist guestWishlist = guestWishlistOpt.get();
            for (WishlistItem guestItem : guestWishlist.getItems()) {
                ProductVariant variant = guestItem.getVariant();
                if (variant.isActive() && variant.getProduct() != null && variant.getProduct().isCurrentlyPublishable()) {
                    if (wishlistItemRepository.findByWishlistAndVariant(customerWishlist, variant).isEmpty()) {
                        WishlistItem mergedItem = new WishlistItem(customerWishlist, variant);
                        wishlistItemRepository.save(mergedItem);
                        customerWishlist.getItems().add(mergedItem);
                    }
                }
            }
            wishlistRepository.delete(guestWishlist);
        }

        return mapToDto(customerWishlist);
    }

    private WishlistDto mapToDto(Wishlist wishlist) {
        List<WishlistItemDto> itemDtos = wishlist.getItems().stream().map(item -> {
            ProductVariant variant = item.getVariant();
            Inventory inv = inventoryService.getOrCreateInventory(variant);
            String imageUrl = variant.getProduct() != null && variant.getProduct().getMedia() != null
                    ? variant.getProduct().getMedia().stream().filter(ProductMedia::isCover).findFirst()
                    .map(ProductMedia::getMediaUrl).orElse(null)
                    : null;

            return new WishlistItemDto(
                    item.getId(),
                    variant.getId(),
                    variant.getSku(),
                    variant.getProduct() != null ? variant.getProduct().getName() : "",
                    variant.getProduct() != null ? variant.getProduct().getSlug() : "",
                    variant.getName(),
                    variant.getPrice(),
                    imageUrl,
                    inv.getAvailable() > 0,
                    item.getCreatedAt()
            );
        }).collect(Collectors.toList());

        return new WishlistDto(wishlist.getId(), itemDtos);
    }
}
