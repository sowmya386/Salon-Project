package com.salon.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;

    public ProductResponse(Long id, String name, String description,
                           Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    // getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getStock() { return stock; }
}
