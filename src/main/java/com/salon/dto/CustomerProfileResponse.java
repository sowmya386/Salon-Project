package com.salon.dto;

public class CustomerProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String salonName;

    public CustomerProfileResponse(Long id,
                                   String fullName,
                                   String email,
                                   String phone,
                                   String salonName) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.salonName = salonName;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSalonName() { return salonName; }
}
