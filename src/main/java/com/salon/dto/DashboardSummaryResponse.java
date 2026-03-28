package com.salon.dto;

public class DashboardSummaryResponse {

    private long bookingsToday;
    private long bookingsThisWeek;
    private long completedBookings;
    private long cancelledBookings;
    private double totalRevenue;
    private long newCustomers;

    public DashboardSummaryResponse(long bookingsToday,
                                    long bookingsThisWeek,
                                    long completedBookings,
                                    long cancelledBookings,
                                    double totalRevenue,
                                    long newCustomers) {
        this.bookingsToday = bookingsToday;
        this.bookingsThisWeek = bookingsThisWeek;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.totalRevenue = totalRevenue;
        this.newCustomers = newCustomers;
    }

    public long getBookingsToday() { return bookingsToday; }
    public long getBookingsThisWeek() { return bookingsThisWeek; }
    public long getCompletedBookings() { return completedBookings; }
    public long getCancelledBookings() { return cancelledBookings; }
    public double getTotalRevenue() { return totalRevenue; }
    public long getNewCustomers() { return newCustomers; }
}
