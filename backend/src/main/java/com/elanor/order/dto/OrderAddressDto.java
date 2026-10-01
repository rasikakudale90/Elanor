package com.elanor.order.dto;

import com.elanor.order.entity.OrderAddress;
import java.util.UUID;

public class OrderAddressDto {
    private UUID id;
    private String fullName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public OrderAddressDto() {}

    public OrderAddressDto(OrderAddress address) {
        if (address != null) {
            this.id = address.getId();
            this.fullName = address.getFullName();
            this.phone = address.getPhone();
            this.addressLine1 = address.getAddressLine1();
            this.addressLine2 = address.getAddressLine2();
            this.city = address.getCity();
            this.state = address.getState();
            this.postalCode = address.getPostalCode();
            this.country = address.getCountry();
        }
    }

    public OrderAddressDto(String fullName, String phone, String addressLine1, String addressLine2,
                           String city, String state, String postalCode, String country) {
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
