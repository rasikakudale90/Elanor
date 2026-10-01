package com.elanor.cart.repository;

import com.elanor.cart.entity.Cart;
import com.elanor.customer.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByCustomer(CustomerProfile customer);
    Optional<Cart> findByCustomerId(UUID customerId);
    Optional<Cart> findByGuestToken(String guestToken);
}
