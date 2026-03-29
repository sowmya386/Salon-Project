package com.salon.service;

import com.salon.dto.*;
import com.salon.entity.*;
import com.salon.repository.*;

import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ServiceRepository serviceRepository;
    private final SalonService salonService;
    private final UserRepository userRepository;

    public DashboardService(BookingRepository bookingRepository,
                            InvoiceRepository invoiceRepository,
                            InvoiceItemRepository invoiceItemRepository,
                            ServiceRepository serviceRepository,
                            SalonService salonService,
                            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.serviceRepository = serviceRepository;
        this.salonService = salonService;
        this.userRepository = userRepository;
    }

    public DashboardSummaryResponse getSummary() {
        String salonName = salonService.getCurrentSalon().getName();

        LocalDate today = LocalDate.now();
        LocalDateTime startToday = today.atStartOfDay();
        LocalDateTime endToday = today.plusDays(1).atStartOfDay();

        LocalDateTime startWeek = today.minusDays(6).atStartOfDay();

        long bookingsToday =
                bookingRepository.countBySalonNameAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(
                        salonName, startToday, endToday);

        long completed =
                bookingRepository.countBySalonNameAndStatus(
                        salonName, BookingStatus.COMPLETED);

        long bookingsThisWeek =
        		 bookingRepository.countBySalonNameAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(
                		salonName, startWeek, endToday);

        long cancelled =
        		 bookingRepository.countBySalonNameAndStatus(
                		salonName, BookingStatus.CANCELLED);

        double revenue = invoiceRepository.getTotalRevenue(salonName);
        long totalCustomers = userRepository.countNewCustomersBySalonName(salonName, startWeek, endToday);

        // Calculate 7-day revenue for the dashboard chart
        java.util.List<java.util.Map<String, Object>> weeklyRevenueMap = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("EEE");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDateTime startOfDay = day.atStartOfDay();
            LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
            
            double dailyRev = invoiceRepository.getRevenueBetween(salonName, startOfDay, endOfDay);
            
            java.util.Map<String, Object> dayData = new java.util.HashMap<>();
            dayData.put("name", day.format(dayFormatter)); // e.g. "Mon"
            dayData.put("revenue", dailyRev);
            weeklyRevenueMap.add(dayData);
        }

        return new DashboardSummaryResponse(
                bookingsToday,
                bookingsThisWeek,
                completed,
                cancelled,
                revenue,
                totalCustomers,
                weeklyRevenueMap
        );
    }

    public List<TopItemResponse> getTopProducts() {
        String salonName = salonService.getCurrentSalon().getName();
        return invoiceItemRepository.findTopProducts(salonName);
    }

    public List<TopItemResponse> getTopServices() {
        String salonName = salonService.getCurrentSalon().getName();
        return serviceRepository.findTopServices(salonName);
    }
}
