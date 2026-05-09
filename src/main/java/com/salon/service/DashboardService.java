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

    public DashboardSummaryResponse getSummary(String filter) {
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

        java.util.List<java.util.Map<String, Object>> chartData = new java.util.ArrayList<>();
        
        if ("year".equalsIgnoreCase(filter)) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM");
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.from(today.minusMonths(i));
                LocalDateTime start = month.atDay(1).atStartOfDay();
                LocalDateTime end = month.atEndOfMonth().plusDays(1).atStartOfDay();
                double rev = invoiceRepository.getRevenueBetween(salonName, start, end);
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("name", month.format(formatter));
                data.put("revenue", rev);
                chartData.add(data);
            }
        } else if ("month".equalsIgnoreCase(filter)) {
            for (int i = 3; i >= 0; i--) {
                LocalDate startDay = today.minusDays((i + 1) * 7 - 1);
                LocalDateTime start = startDay.atStartOfDay();
                LocalDateTime end = startDay.plusDays(7).atStartOfDay();
                double rev = invoiceRepository.getRevenueBetween(salonName, start, end);
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("name", "Week " + (4 - i));
                data.put("revenue", rev);
                chartData.add(data);
            }
        } else if ("all".equalsIgnoreCase(filter)) {
            for (int i = 4; i >= 0; i--) {
                int year = today.getYear() - i;
                LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
                LocalDateTime end = LocalDateTime.of(year + 1, 1, 1, 0, 0);
                double rev = invoiceRepository.getRevenueBetween(salonName, start, end);
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("name", String.valueOf(year));
                data.put("revenue", rev);
                chartData.add(data);
            }
        } else {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEE");
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                LocalDateTime startOfDay = day.atStartOfDay();
                LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
                double dailyRev = invoiceRepository.getRevenueBetween(salonName, startOfDay, endOfDay);
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("name", day.format(formatter));
                data.put("revenue", dailyRev);
                chartData.add(data);
            }
        }

        return new DashboardSummaryResponse(
                bookingsToday,
                bookingsThisWeek,
                completed,
                cancelled,
                revenue,
                totalCustomers,
                chartData
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

    public List<TopCustomerResponse> getTopCustomers() {
        String salonName = salonService.getCurrentSalon().getName();
        return invoiceRepository.findTopCustomersBySpend(salonName, org.springframework.data.domain.PageRequest.of(0, 5))
            .stream()
            .map(row -> new TopCustomerResponse(
                (Long) row[0],
                (String) row[1],
                (String) row[2],
                (Double) row[3],
                ((Number) row[4]).longValue() 
            ))
            .toList();
    }
}
