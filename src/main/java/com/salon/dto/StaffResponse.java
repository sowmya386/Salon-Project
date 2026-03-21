package com.salon.dto;

public record StaffResponse(Long id, String fullName, String email, String phone, Double commissionPercent, boolean active) {}
