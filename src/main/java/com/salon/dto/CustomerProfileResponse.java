package com.salon.dto;

public class CustomerProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String salonName;
    private String homeAddress;
    private String profileImageUrl;

    public CustomerProfileResponse(Long id,
                                   String fullName,
                                   String email,
                                   String phone,
                                   String salonName,
                                   String homeAddress,
                                   String profileImageUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.salonName = salonName;
        this.homeAddress = homeAddress;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSalonName() { return salonName; }
    public String getHomeAddress() { return homeAddress; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
