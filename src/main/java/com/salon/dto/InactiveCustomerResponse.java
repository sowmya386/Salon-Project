package com.salon.dto;

import java.time.LocalDateTime;

public class InactiveCustomerResponse {

    private Long customerId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime lastVisit;
    private Long totalVisits;

    public InactiveCustomerResponse(
            Long customerId,
            String fullName,
            String email,
            String phone,
            LocalDateTime lastVisit,
            Long totalVisits) {

        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.lastVisit = lastVisit;
        this.totalVisits = totalVisits;
    }

    public Long getCustomerId() { return customerId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDateTime getLastVisit() { return lastVisit; }
    public Long getTotalVisits() { return totalVisits; }
}
