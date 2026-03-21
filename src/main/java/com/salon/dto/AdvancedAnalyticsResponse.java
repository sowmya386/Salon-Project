package com.salon.dto;

import java.util.List;

/** Advanced analytics for premium dashboards - revenue growth, trends */
public record AdvancedAnalyticsResponse(
        double totalRevenue,
        double revenueThisMonth,
        double revenueLastMonth,
        double revenueGrowthPercent,
        long totalCustomers,
        long newCustomersThisMonth,
        double avgTransactionValue,
        List<RevenueByDay> revenueByDay,
        List<TopCustomerDto> topCustomersBySpend
) {
    public record RevenueByDay(String date, double revenue) {}
    public record TopCustomerDto(Long id, String name, String email, double totalSpent, int visitCount) {}
}
