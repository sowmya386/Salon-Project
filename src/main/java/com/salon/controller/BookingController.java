package com.salon.controller;

import com.salon.dto.BookingRequest;
import com.salon.dto.BookingResponse;
import com.salon.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ================= CUSTOMER =================
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(pageable));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(bookingService.cancelBookingByAdmin(bookingId));
        } else {
            return ResponseEntity.ok(bookingService.cancelBookingByCustomer(bookingId));
        }
    }
      
    
    // ================= ADMIN =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<BookingResponse>> getAllBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.getSalonBookings(pageable));
    }

    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }
}