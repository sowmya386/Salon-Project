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

    public BookingService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository,
            AuditLogService auditLogService,
            SalonRepository salonRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.salonRepository=salonRepository;
    }

    // ================= CREATE BOOKING =================
    public BookingResponse createBooking(BookingRequest request) {

        Long customerId = SecurityUtil.getCurrentUserId();
        Long salonId = SecurityUtil.getCurrentSalonId();

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        Service service = serviceRepository
                .findByIdAndSalonId(request.getServiceId(), salonId)
                .orElseThrow(() -> new RuntimeException("Service not found in this salon"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setSalon(salon);
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
        Long salonId = SecurityUtil.getCurrentSalonId();

        return bookingRepository
                .findByCustomerIdAndSalonId(customerId, salonId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<BookingResponse> getCustomerBookings(Pageable pageable) {

        Long customerId = SecurityUtil.getCurrentUserId();
        Long salonId = SecurityUtil.getCurrentSalonId();

        return bookingRepository
                .findByCustomerIdAndSalonId(customerId, salonId, pageable)
                .map(this::mapToResponse);
    }

    
    public List<BookingResponse> getSalonBookings() {

    	Long salonId = SecurityUtil.getCurrentSalonId();

        return bookingRepository
                .findBySalonId(salonId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<BookingResponse> getSalonBookings(Pageable pageable) {

        Long salonId = SecurityUtil.getCurrentSalonId();

        return bookingRepository
                .findBySalonId(salonId, pageable)
                .map(this::mapToResponse);
    }

    // ================= CUSTOMER — CANCEL =================
    public BookingResponse cancelBookingByCustomer(Long bookingId) {

        Long customerId = SecurityUtil.getCurrentUserId();
        Long salonId = SecurityUtil.getCurrentSalonId();

        Booking booking = bookingRepository
                .findByIdAndSalonId(bookingId, salonId)
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

        Long salonId = SecurityUtil.getCurrentSalonId();

        Booking booking = bookingRepository
                .findByIdAndSalonId(bookingId, salonId)
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

        Long salonId = SecurityUtil.getCurrentSalonId();

        Booking booking = bookingRepository
                .findByIdAndSalonId(bookingId, salonId)
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
                booking.getAppointmentTime(),
                booking.getStatus()
        );
    }
}
