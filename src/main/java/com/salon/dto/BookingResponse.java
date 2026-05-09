package com.salon.dto;

import com.salon.entity.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private java.util.List<String> serviceNames;
    private String customerName;
    private LocalDateTime appointmentTime;
    private BookingStatus status;
    private String address;
    private String cancellationMessage;
    private String salonName;

    public BookingResponse(Long bookingId,
                           java.util.List<String> serviceNames,
                           String customerName,
                           LocalDateTime appointmentTime,
                           BookingStatus status,
                           String address,
                           String cancellationMessage,
                           String salonName) {
        this.bookingId = bookingId;
        this.serviceNames = serviceNames;
        this.customerName = customerName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.address = address;
        this.cancellationMessage = cancellationMessage;
        this.salonName = salonName;
    }

    public Long getBookingId() { return bookingId; }
    public java.util.List<String> getServiceNames() { return serviceNames; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public BookingStatus getStatus() { return status; }
    public String getAddress() { return address; }
    public String getCancellationMessage() { return cancellationMessage; }
    public String getSalonName() { return salonName; }
}
