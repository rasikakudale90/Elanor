package com.elanor.wishlist.repository;

import com.elanor.customer.entity.CustomerProfile;
import com.elanor.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByCustomer(CustomerProfile customer);
    Optional<Wishlist> findByCustomerId(UUID customerId);
    Optional<Wishlist> findByGuestToken(String guestToken);
}
