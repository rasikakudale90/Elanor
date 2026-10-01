package com.elanor.customer.service;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.dto.*;
import com.elanor.customer.entity.Address;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.AddressRepository;
import com.elanor.customer.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerProfileRepository customerProfileRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerProfileRepository customerProfileRepository, AddressRepository addressRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    public CustomerProfileDto getProfile(UUID userId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        return mapToDto(profile);
    }

    @Transactional
    public CustomerProfileDto updateProfile(UUID userId, UpdateProfileRequest request) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        profile.setFirstName(request.getFirstName().trim());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setBirthDate(request.getBirthDate());
        profile.setGender(request.getGender());

        CustomerProfile saved = customerProfileRepository.save(profile);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getAddresses(UUID userId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        return addressRepository.findByCustomer(profile).stream()
                .map(this::mapToAddressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto createAddress(UUID userId, CreateAddressRequest request) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        List<Address> existing = addressRepository.findByCustomer(profile);
        boolean isFirst = existing.isEmpty();
        boolean shouldBeDefault = request.isDefault() || isFirst;

        if (shouldBeDefault && !existing.isEmpty()) {
            for (Address a : existing) {
                if (a.isDefault()) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            }
        }

        Address address = new Address();
        address.setCustomer(profile);
        address.setFullName(request.getFullName().trim());
        address.setPhone(request.getPhone().trim());
        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry() != null ? request.getCountry().trim() : "India");
        address.setDefault(shouldBeDefault);

        Address saved = addressRepository.save(address);
        return mapToAddressDto(saved);
    }

    @Transactional
    public AddressDto updateAddress(UUID userId, UUID addressId, UpdateAddressRequest request) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        Address address = addressRepository.findByIdAndCustomer(addressId, profile)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Address not found or does not belong to you."));

        if (request.isDefault() && !address.isDefault()) {
            List<Address> existing = addressRepository.findByCustomer(profile);
            for (Address a : existing) {
                if (a.isDefault()) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            }
        }

        address.setFullName(request.getFullName().trim());
        address.setPhone(request.getPhone().trim());
        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry() != null ? request.getCountry().trim() : "India");
        address.setDefault(request.isDefault());

        Address saved = addressRepository.save(address);
        return mapToAddressDto(saved);
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        Address address = addressRepository.findByIdAndCustomer(addressId, profile)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Address not found or does not belong to you."));

        addressRepository.delete(address);
    }

    private CustomerProfileDto mapToDto(CustomerProfile profile) {
        return new CustomerProfileDto(
                profile.getId(),
                profile.getUser() != null ? profile.getUser().getId() : null,
                profile.getUser() != null ? profile.getUser().getEmail() : null,
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAvatarUrl(),
                profile.getBirthDate(),
                profile.getGender()
        );
    }

    private AddressDto mapToAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}
