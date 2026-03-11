package com.salon.dto;

public class ServiceResponse {

    private Long id;
    private String name;
    private Double price;
    private Integer durationInMinutes;

    public ServiceResponse(Long id, String name, Double price, Integer durationInMinutes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationInMinutes = durationInMinutes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

}
