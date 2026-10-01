package com.elanor.customer.repository;

import com.elanor.auth.entity.User;
import com.elanor.customer.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    Optional<CustomerProfile> findByUser(User user);
    Optional<CustomerProfile> findByUserId(UUID userId);
}
