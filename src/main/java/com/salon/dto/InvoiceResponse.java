package com.salon.dto;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceResponse {

    private Long invoiceId;
    private Double totalAmount;
    private String paymentMode;
    private LocalDateTime createdAt;
    private List<InvoiceItemResponse> items;

    public InvoiceResponse(Long invoiceId, Double totalAmount,
                           String paymentMode, LocalDateTime createdAt,
                           List<InvoiceItemResponse> items) {
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getInvoiceId() { return invoiceId; }
    public Double getTotalAmount() { return totalAmount; }
    public String getPaymentMode() { return paymentMode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<InvoiceItemResponse> getItems() { return items; }
}
