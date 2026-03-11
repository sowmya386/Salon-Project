package com.salon.dto;

import jakarta.validation.constraints.*;

public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull @Positive
    private Double price;

    @NotNull @Min(0)
    private Integer stock;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
