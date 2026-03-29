package com.salon.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {

	@NotNull(message = "ServiceId is required")
    private Long serviceId;

    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
}
