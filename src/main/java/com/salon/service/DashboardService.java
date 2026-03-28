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

        LocalDateTime startWeek = today.minusDays(7).atStartOfDay();

        long bookingsToday =
                bookingRepository.countBySalonNameAndAppointmentTimeBetween(
                        salonName, startToday, endToday);

        long completed =
                bookingRepository.countBySalonNameAndStatus(
                        salonName, BookingStatus.COMPLETED);

        long bookingsThisWeek =
        		 bookingRepository.countBySalonNameAndAppointmentTimeBetween(
                		salonName, startWeek, endToday);

        long cancelled =
        		 bookingRepository.countBySalonNameAndStatus(
                		salonName, BookingStatus.CANCELLED);

        double revenue = invoiceRepository.getTotalRevenue(salonName);
        
        long totalCustomers = userRepository.countCustomersBySalonName(salonName);

        return new DashboardSummaryResponse(
                bookingsToday,
                bookingsThisWeek,
                completed,
                cancelled,
                revenue,
                totalCustomers
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
