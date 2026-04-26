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

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<com.salon.dto.CustomerProfileResponse>> getCustomers(Pageable pageable) {
        return ResponseEntity.ok(userService.getSalonCustomers(pageable));
    }

    @PutMapping("/salon-settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.salon.entity.Salon> updateSalonSettings(
            @Valid @RequestBody com.salon.dto.SalonSettingsRequest request) {
        return ResponseEntity.ok(salonService.updateSalonSettings(request));
    }


    // ADMIN creates service
    @PostMapping("/services")
    public ResponseEntity<ServiceResponse> createService(
    		@Valid @RequestBody ServiceRequest request) {

        return ResponseEntity.ok(serviceService.createService(request));
    }

    @PutMapping("/services/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(serviceId, request));
    }
    
    @DeleteMapping("/services/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> deleteService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(serviceService.deactivateService(serviceId));
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
            @PathVariable Long bookingId,
            @RequestBody(required = false) java.util.Map<String, String> payload) {
        
        String msg = payload != null ? payload.get("message") : null;

        return ResponseEntity.ok(
                bookingService.cancelBookingByAdmin(bookingId, msg)
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

    @PutMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> deleteProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(productService.deactivateProduct(productId));
    }

    @PostMapping("/invoices/manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> createManualInvoice(
            @Valid @RequestBody com.salon.dto.ManualInvoiceRequest request) {

        return ResponseEntity.ok(billingService.createManualInvoice(request));
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


    @GetMapping("/dashboard/top-customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.salon.dto.TopCustomerResponse>> getTopCustomers() {
        return ResponseEntity.ok(dashboardService.getTopCustomers());
    }

    @GetMapping("/pending-admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.salon.dto.CustomerProfileResponse>> getPendingAdmins() {
        return ResponseEntity.ok(userService.getPendingAdminsForSalon());
    }

    @PutMapping("/pending-admins/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approvePendingAdmin(@PathVariable Long id) {
        userService.approvePendingAdminLocally(id);
        return ResponseEntity.ok("Admin formally approved for the salon.");
    }
}
