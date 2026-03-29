package com.salon.dto;

public class TopCustomerResponse {
    private Long id;
    private String name;
    private String email;
    private Double totalSpend;
    private Long totalInvoices;

    public TopCustomerResponse(Long id, String name, String email, Double totalSpend, Long totalInvoices) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalSpend = totalSpend;
        this.totalInvoices = totalInvoices;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Double getTotalSpend() { return totalSpend; }
    public Long getTotalInvoices() { return totalInvoices; }
}
