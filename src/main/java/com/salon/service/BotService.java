package com.salon.service;

import com.salon.dto.BotResponse;
import com.salon.repository.ProductRepository;
import com.salon.repository.SalonRepository;
import com.salon.repository.ServiceRepository;
import com.salon.entity.Salon;
import com.salon.security.SecurityUtil;
import com.salon.entity.Booking;
import com.salon.entity.User;
import com.salon.repository.BookingRepository;
import com.salon.repository.UserRepository;


import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class BotService {

    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;
    private final SalonRepository salonRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BotService(ServiceRepository serviceRepository,
                      ProductRepository productRepository,
                      SalonRepository salonRepository,
                      BookingRepository bookingRepository,
                      UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.productRepository = productRepository;
        this.salonRepository = salonRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public BotResponse reply(String message) {

        String lower = message.toLowerCase();
        Long salonId = SecurityUtil.getCurrentSalonId();
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        // 👇 NEW: booking status / next appointment
        if (lower.contains("my booking") || lower.contains("booking status")
                || lower.contains("next appointment")) {

            Long userId = SecurityUtil.getCurrentUserId();
            User customer = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            
            

            Booking next = bookingRepository
                    .findTopByCustomer_IdAndSalon_IdOrderByAppointmentTimeDesc(userId, salonId)
                    .orElse(null);


            if (next == null) {
                return new BotResponse("You have no bookings yet.");
            }

            return new BotResponse(
                    "Your latest booking:\n" +
                    "Service: " + next.getService().getName() + "\n" +
                    "Time: " + next.getAppointmentTime() + "\n" +
                    "Status: " + next.getStatus()
            );
        }

        if (lower.contains("product")) {

        	String products = productRepository
        	        .findBySalon_IdAndActiveTrue(salonId)
        	        .stream()
        	        .map(p -> p.getName() + " - ₹" + p.getPrice())
        	        .collect(Collectors.joining("\n"));

            return new BotResponse(
                    products.isEmpty()
                            ? "No products available."
                            : "Available products:\n" + products
            );
        }

        if (lower.contains("book") || lower.contains("appointment")) {
            return new BotResponse(
                    "You can book an appointment from the app by selecting a service and preferred time."
            );
        }

        if (lower.contains("offer")) {
            return new BotResponse(
                    "Currently there are no active offers. Please check back later."
            );
        }
        if (lower.contains("help") || lower.contains("what can you do")) {
            return new BotResponse(
                "I can help you with:\n" +
                "- Services & prices\n" +
                "- Products\n" +
                "- Booking information\n" +
                "- Your booking status\n" +
                "Try typing: services, products, my booking"
            );
        }

        return new BotResponse(
                "Sorry, I didn’t understand that.\n" +
                "Type 'help' to see what I can assist with."
            );
        }
    }

