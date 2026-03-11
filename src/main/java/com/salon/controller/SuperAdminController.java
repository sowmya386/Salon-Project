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
    @PutMapping("/salons/{salonId}/approve")
    public ResponseEntity<?> approveSalon(@PathVariable Long salonId) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        salon.setApprovalStatus(ApprovalStatus.APPROVED);
        salonRepository.save(salon);

        userRepository.approveAdminsBySalon(salon);

        return ResponseEntity.ok("Salon and admin approved");
    }

    // 3️⃣ Reject salon
    @PutMapping("/salons/{salonId}/reject")
    public ResponseEntity<?> rejectSalon(@PathVariable Long salonId) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        salon.setApprovalStatus(ApprovalStatus.REJECTED);
        salonRepository.save(salon);

        return ResponseEntity.ok("Salon rejected");
    }
}
