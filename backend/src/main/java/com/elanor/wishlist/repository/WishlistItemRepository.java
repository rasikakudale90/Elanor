package com.elanor.wishlist.repository;

import com.elanor.catalog.entity.ProductVariant;
import com.elanor.wishlist.entity.Wishlist;
import com.elanor.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    Optional<WishlistItem> findByWishlistAndVariant(Wishlist wishlist, ProductVariant variant);
    Optional<WishlistItem> findByWishlistAndVariantId(Wishlist wishlist, UUID variantId);
    void deleteByWishlistAndVariantId(Wishlist wishlist, UUID variantId);
}
