package com.salon.service;

import com.salon.entity.Salon;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.SalonRepository;
import com.salon.entity.ApprovalStatus;
import com.salon.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalonService {

    private final SalonRepository salonRepository;

    /** Default salon name for single-tenant mode */
    @Value("${salon.default-name:Default}")
    private String defaultSalonName;

    public SalonService(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
    }

    /** Resolve current salon from context (set by JWT) - for tenant-scoped operations */
    public Salon getCurrentSalon() {
        String salonName;
        try {
            salonName = SecurityUtil.getCurrentSalonName();
        } catch (Exception e) {
            // Fallback to default salon for public unauthenticated requests
            return getDefaultSalon();
        }
        
        return salonRepository.findByNameIgnoreCase(salonName)
                .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", salonName));
    }

    /** Get the single default salon (for single-tenant when no JWT context, e.g. public registration) */
    public Salon getDefaultSalon() {
        return salonRepository.findByNameIgnoreCase(defaultSalonName)
                .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", defaultSalonName));
    }

    public Salon createSalon(Salon salon) {
        salon.setActive(true);
        salon.setCreatedAt(LocalDateTime.now());
        return salonRepository.save(salon);
    }

    public List<Salon> getAllApprovedSalons() {
        return salonRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
    }
}
