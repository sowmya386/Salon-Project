package com.salon.service;

import com.salon.dto.*;
import com.salon.entity.*;
import com.salon.repository.*;
import com.salon.security.SecurityUtil;

import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ServiceRepository serviceRepository;
    private final SalonRepository salonRepository;

    public DashboardService(BookingRepository bookingRepository,
                            InvoiceRepository invoiceRepository,
                            InvoiceItemRepository invoiceItemRepository,
                            ServiceRepository serviceRepository,
                            SalonRepository salonRepository) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.serviceRepository = serviceRepository;
        this.salonRepository = salonRepository;
    }

    public DashboardSummaryResponse getSummary() {

        Salon salon = salonRepository.findById(
                SecurityUtil.getCurrentSalonId()
        ).orElseThrow(() -> new RuntimeException("Salon not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime startToday = today.atStartOfDay();
        LocalDateTime endToday = today.plusDays(1).atStartOfDay();

        LocalDateTime startWeek = today.minusDays(7).atStartOfDay();

        
        Long salonId = SecurityUtil.getCurrentSalonId();

        long bookingsToday =
                bookingRepository.countBySalon_IdAndAppointmentTimeBetween(
                        salonId, startToday, endToday);

        long completed =
                bookingRepository.countBySalon_IdAndStatus(
                        salonId, BookingStatus.COMPLETED);

        

        long bookingsThisWeek =
        		 bookingRepository.countBySalon_IdAndAppointmentTimeBetween(
                		salonId, startWeek, endToday);

        

        long cancelled =
        		 bookingRepository.countBySalon_IdAndStatus(
                		salonId, BookingStatus.CANCELLED);

        double revenue = invoiceRepository.getTotalRevenue(salonId);

        return new DashboardSummaryResponse(
                bookingsToday,
                bookingsThisWeek,
                completed,
                cancelled,
                revenue
        );
    }

    public List<TopItemResponse> getTopProducts() {
        Salon salon = salonRepository.findById(
                SecurityUtil.getCurrentSalonId()
        ).orElseThrow(() -> new RuntimeException("Salon not found"));
        
        Long salonId = SecurityUtil.getCurrentSalonId();

        return invoiceItemRepository.findTopProducts(salonId);
    }

    public List<TopItemResponse> getTopServices() {
        Salon salon = salonRepository.findById(
                SecurityUtil.getCurrentSalonId()
        ).orElseThrow(() -> new RuntimeException("Salon not found"));
        
        
        Long salonId = SecurityUtil.getCurrentSalonId();
        return serviceRepository.findTopServices(salonId);

    }
}
