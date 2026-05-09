package com.salon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_salon_name", columnList = "salon_name"),
        @Index(name = "idx_bookings_customer_id", columnList = "customer_id"),
        @Index(name = "idx_bookings_status", columnList = "status"),
        @Index(name = "idx_bookings_appointment_time", columnList = "appointment_time")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who booked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // what is booked
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "booking_services",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private java.util.List<Service> services;

    // tenant boundary
    @Column(name = "salon_name", nullable = false)
    private String salonName;

    // Optional address for Home Services
    private String address;

    @Column(columnDefinition = "TEXT")
    private String cancellationMessage;

    // when
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.BOOKED;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public java.util.List<Service> getServices() {
        return services;
    }

    public void setServices(java.util.List<Service> services) {
        this.services = services;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCancellationMessage() { return cancellationMessage; }
    public void setCancellationMessage(String cancellationMessage) { this.cancellationMessage = cancellationMessage; }
}
