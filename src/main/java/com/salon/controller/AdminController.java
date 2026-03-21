package com.salon.controller;

import com.salon.dto.BillingRequest;
import com.salon.dto.BookingResponse;

import com.salon.dto.CustomerRegisterRequest;
import com.salon.dto.DashboardSummaryResponse;
import com.salon.dto.InvoiceResponse;
import com.salon.dto.ProductRequest;
import com.salon.dto.ProductResponse;
import com.salon.dto.ServiceRequest;
import com.salon.dto.ServiceResponse;
import com.salon.dto.TopItemResponse;
import com.salon.service.BillingService;
import com.salon.service.BookingService;
import com.salon.service.SalonService;
import com.salon.service.DashboardService;
import com.salon.service.ProductService;
import com.salon.service.ReengagementService;
import com.salon.service.ServiceService;
import com.salon.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.salon.audit.AuditLogResponse;
import com.salon.audit.AuditLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")              // ✅ FIXED
@PreAuthorize("hasRole('ADMIN')")          // ✅ CLASS-LEVEL ONLY
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final BookingService bookingService;
    private final AuditLogService auditLogService;
    private final ProductService productService;
    private final BillingService billingService;
    private final ReengagementService reengagementService;
    private final DashboardService dashboardService;
    private final SalonService salonService;

    public AdminController(UserService userService,
                           ServiceService serviceService,
                           BookingService bookingService,
                           AuditLogService auditLogService,
                           ProductService productService,
                           BillingService billingService,
                           ReengagementService reengagementService,
                           DashboardService dashboardService,
                           SalonService salonService) {
    	
        this.userService = userService;
        this.serviceService = serviceService;
        this.bookingService = bookingService;
        this.auditLogService = auditLogService;
        this.productService = productService;
        this.billingService = billingService;
        this.reengagementService=reengagementService;
        this.dashboardService = dashboardService;
        this.salonService = salonService;
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerCustomerByAdmin(
            @Valid @RequestBody CustomerRegisterRequest request) {

        userService.registerCustomerByAdmin(request, salonService.getCurrentSalon());
        return ResponseEntity.ok("Customer registered");
    }


    // ADMIN creates service
    @PostMapping("/services")
    public ResponseEntity<ServiceResponse> createService(
    		@Valid @RequestBody ServiceRequest request) {

        return ResponseEntity.ok(serviceService.createService(request));
    }

    
    @GetMapping("/cust/bookings")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            Pageable pageable) {

        return ResponseEntity.ok(
                bookingService.getCustomerBookings(pageable)
        );
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> getSalonBookings(Pageable pageable) {
        return ResponseEntity.ok(
                bookingService.getSalonBookings(pageable)
        );
    }
    
    @PutMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBookingByAdmin(bookingId)
        );
    }
    @PutMapping("/bookings/{bookingId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.completeBooking(bookingId)
        );
    }
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            Pageable pageable) {

        return ResponseEntity.ok(
                auditLogService.getAuditLogs(pageable)
        );
    }
    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(productService.createProduct(request));
    }
    @PostMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody BillingRequest request) {

        return ResponseEntity.ok(billingService.createInvoice(request));
    }
    @GetMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvoiceResponse>> getInvoices(Pageable pageable) {
        return ResponseEntity.ok(billingService.getSalonInvoices(pageable));
    }
    @GetMapping("/customers/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getInactiveCustomers(
            @RequestParam int days,
            Pageable pageable) {

        return ResponseEntity.ok(
                reengagementService.getInactiveCustomers(days, pageable)
        );
    }
    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/dashboard/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopItemResponse>> getTopProducts() {
        return ResponseEntity.ok(dashboardService.getTopProducts());
    }

    @GetMapping("/dashboard/top-services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopItemResponse>> getTopServices() {
        return ResponseEntity.ok(dashboardService.getTopServices());
    }


   






}
