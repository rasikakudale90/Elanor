package com.elanor.customer.repository;

import com.elanor.customer.entity.Address;
import com.elanor.customer.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomer(CustomerProfile customer);
    Optional<Address> findByIdAndCustomer(UUID id, CustomerProfile customer);
    Optional<Address> findByCustomerAndIsDefaultTrue(CustomerProfile customer);
}
