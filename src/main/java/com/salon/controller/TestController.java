package com.salon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.salon.security.SecurityUtil;

@RestController
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
    
    @GetMapping("/api/test/salon")
    public String testSalon() {
        Long salonId = SecurityUtil.getCurrentSalonId();
        return "Salon ID from JWT = " + salonId;
    }
    
  

        @GetMapping("/api/admin")
        public String adminTest(@RequestAttribute("salonId") Long salonId) {
            return "ADMIN JWT OK | salonId = " + salonId;
        }

        @GetMapping("/api/c")
        public String customerTest(@RequestAttribute("salonId") Long salonId) {
            return "CUSTOMER JWT OK | salonId = " + salonId;
        }
   

}
