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

    @Column(name = "salon_name", nullable = false)
    private String salonName;

    @Column(unique = true)
    private String invoiceNumber;

    @ManyToOne
    private User customer;

    private String customerName; // For walk-ins
    private String customerPhone; // For walk-ins

    @OneToOne
    private Booking booking; // nullable (product-only bill)

    private Double subtotal;
    private Double discount;
    private Double gstTotal;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.PENDING;

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
    public String getSalonName() { return salonName; }
    public void setSalonName(String salonName) { this.salonName = salonName; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getGstTotal() { return gstTotal; }
    public void setGstTotal(Double gstTotal) { this.gstTotal = gstTotal; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
