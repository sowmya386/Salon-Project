package com.salon.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StaffRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email
    private String email;

    private String phone;

    @DecimalMin("0") @DecimalMax("100")
    private Double commissionPercent = 0.0;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getCommissionPercent() { return commissionPercent; }
    public void setCommissionPercent(Double commissionPercent) { this.commissionPercent = commissionPercent; }
}
