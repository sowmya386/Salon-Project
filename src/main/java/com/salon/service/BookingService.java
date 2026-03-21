package com.salon.service;

import com.salon.audit.AuditAction;
import com.salon.audit.AuditLogService;
import com.salon.dto.BookingRequest;
import com.salon.dto.BookingResponse;
import com.salon.entity.Booking;
import com.salon.entity.BookingStatus;
import com.salon.entity.Salon;
import com.salon.entity.Service;
import com.salon.entity.User;
import com.salon.repository.BookingRepository;
import com.salon.repository.SalonRepository;
import com.salon.repository.ServiceRepository;
import com.salon.repository.UserRepository;
import com.salon.security.SecurityUtil;
import com.salon.service.SalonService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final SalonRepository salonRepository;
    private final SalonService salonService;

    public BookingService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository,
            AuditLogService auditLogService,
            SalonRepository salonRepository,
            SalonService salonService
    ) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.salonRepository = salonRepository;
        this.salonService = salonService;
    }

    // ================= CREATE BOOKING =================
    public BookingResponse createBooking(BookingRequest request) {

        Long customerId = SecurityUtil.getCurrentUserId();
        Salon salon = salonService.getCurrentSalon();

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Service service = serviceRepository
                .findByIdAndSalonName(request.getServiceId(), salon.getName())
                .orElseThrow(() -> new RuntimeException("Service not found in this salon"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setSalonName(salon.getName());
        booking.setAppointmentTime(request.getAppointmentTime());
        booking.setStatus(BookingStatus.BOOKED);

        booking = bookingRepository.save(booking);

        auditLogService.log(
                AuditAction.CREATE_BOOKING,
                "Customer created booking ID " + booking.getId()
        );

        return mapToResponse(booking);
    }

    // ================= CUSTOMER — LIST BOOKINGS =================
    public List<BookingResponse> getCustomerBookings() {

        Long customerId = SecurityUtil.getCurrentUserId();
        String salonName = salonService.getCurrentSalon().getName();

        return bookingRepository
                .findByCustomer_IdAndSalonName(customerId, salonName)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<BookingResponse> getCustomerBookings(Pageable pageable) {

        Long customerId = SecurityUtil.getCurrentUserId();
        String salonName = salonService.getCurrentSalon().getName();

        return bookingRepository
                .findByCustomer_IdAndSalonName(customerId, salonName, pageable)
                .map(this::mapToResponse);
    }

    
    public List<BookingResponse> getSalonBookings() {

    	String salonName = salonService.getCurrentSalon().getName();

        return bookingRepository
                .findBySalonName(salonName)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<BookingResponse> getSalonBookings(Pageable pageable) {

        String salonName = salonService.getCurrentSalon().getName();

        return bookingRepository
                .findBySalonName(salonName, pageable)
                .map(this::mapToResponse);
    }

    // ================= CUSTOMER — CANCEL =================
    public BookingResponse cancelBookingByCustomer(Long bookingId) {

        Long customerId = SecurityUtil.getCurrentUserId();
        String salonName = salonService.getCurrentSalon().getName();

        Booking booking = bookingRepository
                .findByIdAndSalonName(bookingId, salonName)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You can cancel only your own booking");
        }

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        auditLogService.log(
                AuditAction.CANCEL_BOOKING,
                "Customer cancelled booking ID " + bookingId
        );

        return mapToResponse(booking);
    }

    // ================= ADMIN — CANCEL =================
    public BookingResponse cancelBookingByAdmin(Long bookingId) {

        String salonName = salonService.getCurrentSalon().getName();

        Booking booking = bookingRepository
                .findByIdAndSalonName(bookingId, salonName)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        auditLogService.log(
                AuditAction.CANCEL_BOOKING,
                "Admin cancelled booking ID " + bookingId
        );

        return mapToResponse(booking);
    }

    // ================= ADMIN — COMPLETE =================
    public BookingResponse completeBooking(Long bookingId) {

        String salonName = salonService.getCurrentSalon().getName();

        Booking booking = bookingRepository
                .findByIdAndSalonName(bookingId, salonName)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Only booked appointments can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        auditLogService.log(
                AuditAction.COMPLETE_BOOKING,
                "Admin completed booking ID " + bookingId	
        );

        return mapToResponse(booking);
    }

    // ================= MAPPER =================
    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getService().getName(),
                booking.getCustomer().getFullName(),
                booking.getAppointmentTime(),
                booking.getStatus()
        );
    }
}
