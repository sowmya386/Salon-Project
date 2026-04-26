package com.salon.dto;

import java.util.List;

public class ManualInvoiceRequest {
    private String customerName;
    private String customerPhone;
    private List<BillingItemRequest> items;
    private Double discount;
    private String paymentMode;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<BillingItemRequest> getItems() { return items; }
    public void setItems(List<BillingItemRequest> items) { this.items = items; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
}
