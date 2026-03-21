package com.salon.controller;

import com.salon.entity.*;
import com.salon.repository.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    public SuperAdminController(
            SalonRepository salonRepository,
            UserRepository userRepository) {

        this.salonRepository = salonRepository;
        this.userRepository = userRepository;
    }

    // 1️⃣ View pending salons
    @GetMapping("/salons/pending")
    public List<Salon> getPendingSalons() {
        return salonRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

 // 2️⃣ Approve salon + admins
    @PutMapping("/salons/{salonName}/approve")
    public ResponseEntity<?> approveSalon(@PathVariable String salonName) {

        Salon salon = salonRepository.findByNameIgnoreCase(salonName)
                .orElseThrow(() -> new RuntimeException("Salon not found: " + salonName));

        salon.setApprovalStatus(ApprovalStatus.APPROVED);
        salonRepository.save(salon);

        userRepository.approveAdminsBySalonName(salon.getName());

        return ResponseEntity.ok("Salon and admin approved");
    }

    // 3️⃣ Reject salon
    @PutMapping("/salons/{salonName}/reject")
    public ResponseEntity<?> rejectSalon(@PathVariable String salonName) {

        Salon salon = salonRepository.findByNameIgnoreCase(salonName)
                .orElseThrow(() -> new RuntimeException("Salon not found: " + salonName));

        salon.setApprovalStatus(ApprovalStatus.REJECTED);
        salonRepository.save(salon);

        return ResponseEntity.ok("Salon rejected");
    }
}