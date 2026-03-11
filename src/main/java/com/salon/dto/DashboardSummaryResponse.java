package com.salon.dto;

public class DashboardSummaryResponse {

    private long bookingsToday;
    private long bookingsThisWeek;
    private long completedBookings;
    private long cancelledBookings;
    private double totalRevenue;

    public DashboardSummaryResponse(long bookingsToday,
                                    long bookingsThisWeek,
                                    long completedBookings,
                                    long cancelledBookings,
                                    double totalRevenue) {
        this.bookingsToday = bookingsToday;
        this.bookingsThisWeek = bookingsThisWeek;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.totalRevenue = totalRevenue;
    }

    public long getBookingsToday() { return bookingsToday; }
    public long getBookingsThisWeek() { return bookingsThisWeek; }
    public long getCompletedBookings() { return completedBookings; }
    public long getCancelledBookings() { return cancelledBookings; }
    public double getTotalRevenue() { return totalRevenue; }
}
