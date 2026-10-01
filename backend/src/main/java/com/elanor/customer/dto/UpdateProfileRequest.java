package com.elanor.customer.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;
    private String phone;
    private String avatarUrl;
    private LocalDate birthDate;
    private String gender;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String firstName, String lastName, String phone, String avatarUrl, LocalDate birthDate, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
