package com.salon.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull(message = "ServiceIds are required")
    private java.util.List<Long> serviceIds;

    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;

    public java.util.List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(java.util.List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    // Optional
    private String address;
    private String paymentMethod;
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    private Integer redeemLoyaltyPoints;
    public Integer getRedeemLoyaltyPoints() { return redeemLoyaltyPoints; }
    public void setRedeemLoyaltyPoints(Integer redeemLoyaltyPoints) { this.redeemLoyaltyPoints = redeemLoyaltyPoints; }
}
