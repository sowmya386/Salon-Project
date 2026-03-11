package com.salon.controller;

import com.salon.dto.BookingRequest;

import com.salon.dto.BookingResponse;
import com.salon.dto.CustomerProfileResponse;
import com.salon.dto.CustomerRegisterRequest;
import com.salon.dto.InvoiceResponse;
import com.salon.dto.ProductResponse;
import com.salon.dto.ServiceResponse;
import com.salon.entity.User;
import com.salon.security.SecurityUtil;
import com.salon.service.BillingService;
import com.salon.service.BookingService;
import com.salon.service.ProductService;
import com.salon.service.ServiceService;
import com.salon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final BookingService bookingService;
    private final ProductService productService;
    private final BillingService billingService;


    public CustomerController(UserService userService,ServiceService serviceService,BookingService bookingService,
    		ProductService productService,BillingService billingService) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.bookingService = bookingService;
        this.productService = productService;
        this.billingService = billingService;
    }

    // ================= CUSTOMER SELF REGISTRATION (PUBLIC) =================
    @PostMapping("/register")
    public ResponseEntity<String> selfRegisterCustomer(
            @Valid @RequestBody CustomerRegisterRequest request) {

        userService.registerCustomerSelf(request);
        return ResponseEntity.ok("Customer registered successfully");
    }


    // ================= CUSTOMER PROFILE (SECURED) =================
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getCustomerProfile() {

        Long userId = SecurityUtil.getCurrentUserId(); // ✅ FIXED

        User customer = userService.getUserById(userId);

        CustomerProfileResponse response =
                new CustomerProfileResponse(
                        customer.getId(),
                        customer.getFullName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getSalon().getName()
                );

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/services")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<ServiceResponse>> getServices(
            Pageable pageable) {

        return ResponseEntity.ok(
                serviceService.getActiveServices(pageable)
        );
    }
    @PostMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
    		@Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(bookingService.createBooking(request));
    }
    
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            Pageable pageable) {

        return ResponseEntity.ok(
                bookingService.getCustomerBookings(pageable)
        );
    }
   


    @PutMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> cancelMyBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBookingByCustomer(bookingId)
        );
    }
    
    @GetMapping("/products")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable) {

        return ResponseEntity.ok(productService.getActiveProducts(pageable));
    }
    @GetMapping("/invoices")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<InvoiceResponse>> getMyInvoices(Pageable pageable) {
        return ResponseEntity.ok(billingService.getCustomerInvoices(pageable));
    }


}
