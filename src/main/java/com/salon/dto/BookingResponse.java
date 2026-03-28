package com.salon.dto;

import com.salon.entity.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private String serviceName;
    private String customerName;
    private LocalDateTime appointmentTime;
    private BookingStatus status;
    private String address;
    private String cancellationMessage;

    public BookingResponse(Long bookingId,
                           String serviceName,
                           String customerName,
                           LocalDateTime appointmentTime,
                           BookingStatus status,
                           String address,
                           String cancellationMessage) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.address = address;
        this.cancellationMessage = cancellationMessage;
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

    public String getAddress() {
        return address;
    }

    public String getCancellationMessage() {
        return cancellationMessage;
    }
}
