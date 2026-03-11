package com.salon.service;

import com.salon.entity.Salon;
import com.salon.repository.SalonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalonService {

    private final SalonRepository salonRepository;

    public SalonService(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
    }

    public Salon createSalon(Salon salon) {
        salon.setActive(true);
        salon.setCreatedAt(LocalDateTime.now());
        return salonRepository.save(salon);
    }
}
