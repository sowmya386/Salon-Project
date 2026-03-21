package com.salon.controller;

import com.salon.security.SecurityUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Test endpoints - only active in dev profile */
@RestController
@Profile("dev")
public class TestController {

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer role access OK";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/test")
    public String adminTest() {
        return "Admin role access OK";
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @GetMapping("/api/test/salon")
    public String testSalon() {
        String salonName = SecurityUtil.getCurrentSalonName();
        return "Salon name from JWT = " + salonName;
    }
   

}
