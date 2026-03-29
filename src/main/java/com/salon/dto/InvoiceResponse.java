package com.salon.dto;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceResponse {

    private Long invoiceId;
    private Double totalAmount;
    private String paymentMode;
    private LocalDateTime createdAt;
    private List<InvoiceItemResponse> items;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    public InvoiceResponse(Long invoiceId, Double totalAmount,
                           String paymentMode, LocalDateTime createdAt,
                           List<InvoiceItemResponse> items,
                           String customerName, String customerEmail, String customerPhone) {
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.createdAt = createdAt;
        this.items = items;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    public Long getInvoiceId() { return invoiceId; }
    public Double getTotalAmount() { return totalAmount; }
    public String getPaymentMode() { return paymentMode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<InvoiceItemResponse> getItems() { return items; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
}
