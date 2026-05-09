package com.salon.service;

import com.salon.entity.Booking;
import com.salon.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(Booking booking) {
        String to = booking.getCustomer().getEmail();
        String subject = "Booking Confirmation - " + booking.getSalonName();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String formattedDate = booking.getAppointmentTime() != null ? booking.getAppointmentTime().format(formatter) : "TBD";

        String body = String.format("Hi %s,\n\nYour booking for '%s' on %s has been confirmed!\n\nThank you for choosing %s.",
                booking.getCustomer().getFullName(),
                booking.getServices().stream().map(com.salon.entity.Service::getName).collect(java.util.stream.Collectors.joining(", ")),
                formattedDate,
                booking.getSalonName());

        sendEmail(to, subject, body);
    }

    public void sendCancellationNotice(Booking booking) {
        String to = booking.getCustomer().getEmail();
        String subject = "Booking Cancellation - " + booking.getSalonName();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String formattedDate = booking.getAppointmentTime() != null ? booking.getAppointmentTime().format(formatter) : "TBD";

        String body = String.format("Hi %s,\n\nYour booking for '%s' on %s has been cancelled.\n\nReason: %s\n\nWe apologize for any inconvenience.",
                booking.getCustomer().getFullName(),
                booking.getServices().stream().map(com.salon.entity.Service::getName).collect(java.util.stream.Collectors.joining(", ")),
                formattedDate,
                booking.getCancellationMessage() != null ? booking.getCancellationMessage() : "No reason provided.");

        sendEmail(to, subject, body);
    }
    
    public void sendReengagementReminder(User customer, String salonName) {
        String to = customer.getEmail();
        String subject = "We miss you at " + salonName + "!";
        String body = String.format("Hi %s,\n\nIt's been a while since your last visit. We'd love to see you back at %s. Book an appointment today and get a special discount!\n\nSee you soon!",
                customer.getFullName(), salonName);

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String text) {
        // Log the email intention for debugging
        logger.info("\n========== [EMAIL OUTBOX] ==========\nTo: {}\nSubject: {}\n\n{}\n====================================", to, subject, text);

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                message.setFrom("noreply@salon-saas.com");
                
                // This will fail with mock credentials, so we silently catch MailException
                mailSender.send(message);
                logger.info("Successfully delivered email via SMTP to {}", to);
            } catch (Exception e) {
                logger.warn("SMTP Delivery Failed (Using mock credentials?): {}", e.getMessage());
            }
        }
    }
}
