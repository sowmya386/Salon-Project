package com.salon.dto;

import com.salon.entity.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private String serviceName;
    private String customerName;
    private LocalDateTime appointmentTime;
    private BookingStatus status;

    public BookingResponse(Long bookingId,
                           String serviceName,
                           String customerName,
                           LocalDateTime appointmentTime,
                           BookingStatus status) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
