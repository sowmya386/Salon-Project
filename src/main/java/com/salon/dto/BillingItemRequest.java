package com.salon.dto;

import jakarta.validation.constraints.*;

public class BillingItemRequest {

    @NotNull
    private Long itemId; // serviceId or productId

    @NotNull
    private String itemType; // SERVICE / PRODUCT

    @Min(1)
    private Integer quantity = 1;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
