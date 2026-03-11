package com.salon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Salon salon;

    @ManyToOne
    private User customer;

    @OneToOne
    private Booking booking; // nullable (product-only bill)

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private LocalDateTime createdAt;

    @OneToMany(
    	    mappedBy = "invoice",
    	    cascade = CascadeType.ALL,
    	    orphanRemoval = true
    	)
    	private List<InvoiceItem> items = new ArrayList<>();


    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters
    public Long getId() { return id; }
    public Salon getSalon() { return salon; }
    public void setSalon(Salon salon) { this.salon = salon; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
