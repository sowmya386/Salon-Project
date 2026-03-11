package com.salon.controller;

import com.salon.entity.Salon;
import com.salon.service.SalonService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salons")

public class SalonController {

    private final SalonService salonService;

    public SalonController(SalonService salonService) {
        this.salonService = salonService;
    }

    @PostMapping
    public Salon createSalon(@RequestBody Salon salon) {
        return salonService.createSalon(salon);
    }
}
