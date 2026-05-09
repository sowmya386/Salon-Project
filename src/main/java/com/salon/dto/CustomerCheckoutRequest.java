package com.salon.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CustomerCheckoutRequest {

    @NotEmpty(message = "Cart cannot be empty")
    private List<CheckoutItem> items;

    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Pincode is required")
    private String pincode;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    public List<CheckoutItem> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItem> items) {
        this.items = items;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static class CheckoutItem {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
