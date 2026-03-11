package com.salon.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class BillingRequest {

    private Long bookingId; // nullable (product-only billing)

    @NotNull
    private String paymentMode; // CASH / CARD / UPI

    @NotEmpty
    private List<BillingItemRequest> items;

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public List<BillingItemRequest> getItems() { return items; }
    public void setItems(List<BillingItemRequest> items) { this.items = items; }
}
