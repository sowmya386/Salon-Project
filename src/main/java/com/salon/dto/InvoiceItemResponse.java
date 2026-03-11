package com.salon.dto;

public class InvoiceItemResponse {

    private String name;
    private String type;
    private Double price;
    private Integer quantity;
    private Double amount;

    public InvoiceItemResponse(String name, String type,
                               Double price, Integer quantity,
                               Double amount) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public Double getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Double getAmount() { return amount; }
}
