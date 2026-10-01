package com.elanor.cart.repository;

import com.elanor.cart.entity.Cart;
import com.elanor.cart.entity.CartItem;
import com.elanor.catalog.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartAndVariant(Cart cart, ProductVariant variant);
    Optional<CartItem> findByCartAndVariantId(Cart cart, UUID variantId);
    void deleteByCartAndVariantId(Cart cart, UUID variantId);
}
