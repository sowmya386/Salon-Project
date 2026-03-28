package com.salon.service;

import com.salon.entity.Booking;
import com.salon.entity.BookingStatus;
import com.salon.entity.Salon;
import com.salon.entity.User;
import com.salon.repository.BookingRepository;
import com.salon.repository.SalonRepository;
import com.salon.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutomatedTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AutomatedTaskScheduler.class);

    private final SalonRepository salonRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AutomatedTaskScheduler(
            SalonRepository salonRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.salonRepository = salonRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Daily at 8 AM: Send reminders for appointments happening tomorrow.
     * Note: "0 0 8 * * *" means 8:00 AM every single day.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyAppointmentReminders() {
        logger.info("[CRON] Starting daily appointment reminders job...");
        LocalDateTime startOfTomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0);
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1).minusSeconds(1);

        List<Salon> salons = salonRepository.findAll();
        for (Salon salon : salons) {
            // Find bookings between start and end of tomorrow for this salon
            List<Booking> upcomingBookings = bookingRepository.findBySalonNameAndAppointmentTimeBetweenAndStatus(
                    salon.getName(), startOfTomorrow, endOfTomorrow, BookingStatus.BOOKED);
            
            for (Booking booking : upcomingBookings) {
                // Technically we'd use a dedicated 'sendReminder' email format, reusing confirmation here to simulate
                emailService.sendBookingConfirmation(booking);
                logger.debug("Sent reminder to {} for booking ID {}", booking.getCustomer().getEmail(), booking.getId());
            }
        }
        logger.info("[CRON] Completed daily appointment reminders job.");
    }

    /**
     * Weekly on Mondays at 10 AM: Send re-engagement emails to inactive customers.
     * Note: "0 0 10 * * MON" means 10:00 AM every Monday.
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyReengagementEmails() {
        logger.info("[CRON] Starting weekly re-engagement job...");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(45); // Not seen in 45 days

        List<Salon> salons = salonRepository.findAll();
        for (Salon salon : salons) {
            try {
                // Just fetch up to 50 inactive customers per salon to avoid spam bursts
                var inactiveRows = bookingRepository.findInactiveCustomers(salon.getName(), cutoffDate, PageRequest.of(0, 50));
                
                for (Object[] row : inactiveRows.getContent()) {
                    Long customerId = (Long) row[0];
                    User customer = userRepository.findById(customerId).orElse(null);
                    if (customer != null) {
                        emailService.sendReengagementReminder(customer, salon.getName());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to process re-engagement for salon: {}", salon.getName(), e);
            }
        }
        logger.info("[CRON] Completed weekly re-engagement job.");
    }
}
