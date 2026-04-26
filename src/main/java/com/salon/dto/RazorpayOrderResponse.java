package com.salon.dto;

public class RazorpayOrderResponse {
    private String orderId;

    public RazorpayOrderResponse(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
