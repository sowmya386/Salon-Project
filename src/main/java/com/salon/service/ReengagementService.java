package com.salon.service;

import com.salon.dto.InactiveCustomerResponse;
import com.salon.repository.BookingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReengagementService {

    private final BookingRepository bookingRepository;
    private final SalonService salonService;

    public ReengagementService(
            BookingRepository bookingRepository,
            SalonService salonService) {

        this.bookingRepository = bookingRepository;
        this.salonService = salonService;
    }

    public Page<InactiveCustomerResponse> getInactiveCustomers(
            int days,
            Pageable pageable) {

        String salonName = salonService.getCurrentSalon().getName();

        LocalDateTime cutoffDate =
                LocalDateTime.now().minusDays(days);


        return bookingRepository
                .findInactiveCustomers(salonName, cutoffDate, pageable)
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
