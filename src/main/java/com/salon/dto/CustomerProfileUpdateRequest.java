package com.salon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 digits")
    private String phone;

    private String homeAddress;
    private String pincode;
    private String profileImageUrl;

    public CustomerProfileUpdateRequest() {}

    public CustomerProfileUpdateRequest(String fullName, String phone, String homeAddress, String pincode, String profileImageUrl) {
        this.fullName = fullName;
        this.phone = phone;
        this.homeAddress = homeAddress;
        this.pincode = pincode;
        this.profileImageUrl = profileImageUrl;
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

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
