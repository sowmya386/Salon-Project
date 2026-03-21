package com.salon.dto;

public record LowStockAlertResponse(Long productId, String productName, int currentStock, int threshold) {}
