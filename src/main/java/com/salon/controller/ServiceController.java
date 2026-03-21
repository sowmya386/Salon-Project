package com.salon.controller;

import com.salon.dto.ServiceRequest;
import com.salon.dto.ServiceResponse;
import com.salon.service.ServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ================= ADMIN ONLY =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ServiceResponse> createService(
            @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.createService(request));
    }

    @DeleteMapping("/{serviceId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ServiceResponse> deactivateService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(serviceService.deactivateService(serviceId));
    }

    // ================= ADMIN + CUSTOMER =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<ServiceResponse>> getActiveServices(
            Pageable pageable) {
        return ResponseEntity.ok(serviceService.getActiveServices(pageable));
    }
}