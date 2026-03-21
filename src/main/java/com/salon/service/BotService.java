package com.salon.service;

import com.salon.dto.BotResponse;
import com.salon.repository.ProductRepository;
import com.salon.repository.ServiceRepository;
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
    private final SalonService salonService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BotService(ServiceRepository serviceRepository,
                      ProductRepository productRepository,
                      SalonService salonService,
                      BookingRepository bookingRepository,
                      UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.productRepository = productRepository;
        this.salonService = salonService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public BotResponse reply(String message) {
        // Bot can be called without auth (permitAll); use default salon for single-tenant
        String salonName;
        Long userId;
        try {
            salonName = salonService.getCurrentSalon().getName();
            userId = SecurityUtil.getCurrentUserId();
        } catch (Exception e) {
            // No auth - use default salon; booking lookup won't work without userId
            salonName = salonService.getDefaultSalon().getName();
            userId = null;
        }

        String lower = message.toLowerCase();

        // 👇 booking status / next appointment (requires auth)
        if (lower.contains("my booking") || lower.contains("booking status")
                || lower.contains("next appointment")) {

            if (userId == null) {
                return new BotResponse("Please sign in to view your booking status.");
            }
            User customer = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            
            

            Booking next = bookingRepository
                    .findTopByCustomer_IdAndSalonNameOrderByAppointmentTimeDesc(userId, salonName)
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

        // Fetch all currently active services and products
        java.util.List<com.salon.entity.Service> allServices = serviceRepository.findBySalonNameAndActiveTrue(salonName);
        java.util.List<com.salon.entity.Product> allProducts = productRepository.findBySalonNameAndActiveTrue(salonName);

        // 👇 Specific item lookup: check if message contains any specific product/service name
        java.util.List<com.salon.entity.Service> matchedServices = allServices.stream()
                .filter(s -> lower.contains(s.getName().toLowerCase()))
                .collect(Collectors.toList());

        java.util.List<com.salon.entity.Product> matchedProducts = allProducts.stream()
                .filter(p -> lower.contains(p.getName().toLowerCase()))
                .collect(Collectors.toList());

        if (!matchedServices.isEmpty() || !matchedProducts.isEmpty()) {
            StringBuilder sb = new StringBuilder("Here is what I found for your query:\n");
            for (com.salon.entity.Service s : matchedServices) {
                sb.append("✨ ").append(s.getName()).append(" - ₹").append(s.getPrice()).append(" (").append(s.getDurationInMinutes() != null ? s.getDurationInMinutes() : 60).append(" mins)\n");
            }
            for (com.salon.entity.Product p : matchedProducts) {
                sb.append("🛍️ ").append(p.getName()).append(" - ₹").append(p.getPrice())
                  .append(p.getStock() > 0 ? " (In Stock)" : " (Out of Stock)").append("\n");
            }
            return new BotResponse(sb.toString());
        }

        // 👇 Generic lists queries
        if (lower.contains("service") || lower.contains("treatment")) {
            String servicesMsg = allServices.stream()
                    .map(s -> "• " + s.getName() + " (₹" + s.getPrice() + ")")
                    .collect(Collectors.joining("\n"));

            return new BotResponse(
                    servicesMsg.isEmpty()
                            ? "Currently, no services are available."
                            : "Here are our premium services:\n" + servicesMsg
            );
        }

        if (lower.contains("product") || lower.contains("retail")) {
        	String products = allProducts.stream()
        	        .map(p -> "• " + p.getName() + " (₹" + p.getPrice() + ")")
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

