package com.salon.dto;

/** Customer loyalty points - 1 point per $1, 100 points = $5 discount */
public record LoyaltyBalanceResponse(int points, double estimatedValue, int visits) {}
