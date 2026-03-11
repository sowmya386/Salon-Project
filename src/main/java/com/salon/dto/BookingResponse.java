package com.salon.dto;

import com.salon.entity.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private String serviceName;
    private LocalDateTime appointmentTime;
    private BookingStatus status;

    public BookingResponse(Long bookingId,
                           String serviceName,
                           LocalDateTime appointmentTime,
                           BookingStatus status) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
