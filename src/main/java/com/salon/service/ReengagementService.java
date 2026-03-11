package com.salon.service;

import com.salon.dto.InactiveCustomerResponse;
import com.salon.entity.Salon;
import com.salon.repository.BookingRepository;
import com.salon.repository.SalonRepository;
import com.salon.security.SecurityUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReengagementService {

    private final BookingRepository bookingRepository;
    private final SalonRepository salonRepository;

    public ReengagementService(
            BookingRepository bookingRepository,
            SalonRepository salonRepository) {

        this.bookingRepository = bookingRepository;
        this.salonRepository = salonRepository;
    }

    public Page<InactiveCustomerResponse> getInactiveCustomers(
            int days,
            Pageable pageable) {

        Long salonId = SecurityUtil.getCurrentSalonId();

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        LocalDateTime cutoffDate =
                LocalDateTime.now().minusDays(days);


        return bookingRepository
                .findInactiveCustomers(salonId, cutoffDate, pageable)
                .map(row -> new InactiveCustomerResponse(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (LocalDateTime) row[4],
                        (Long) row[5]
                ));
    }
}
