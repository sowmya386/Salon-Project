package com.salon.service;

import com.salon.dto.BotResponse;
import com.salon.repository.ProductRepository;
import com.salon.repository.ServiceRepository;
import com.salon.security.SecurityUtil;
import com.salon.entity.Booking;
import com.salon.entity.User;
import com.salon.repository.BookingRepository;
import com.salon.repository.UserRepository;
import com.salon.entity.Role;
import com.salon.dto.DashboardSummaryResponse;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class BotService {

    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;
    private final SalonService salonService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DashboardService dashboardService;

    public BotService(ServiceRepository serviceRepository,
                      ProductRepository productRepository,
                      SalonService salonService,
                      BookingRepository bookingRepository,
                      UserRepository userRepository,
                      DashboardService dashboardService) {
        this.serviceRepository = serviceRepository;
        this.productRepository = productRepository;
        this.salonService = salonService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
    }

    public BotResponse reply(String message) {
        // Bot can be called without auth (permitAll); use default salon for single-tenant
        String salonName;
        Long userId;
        boolean isAdmin = false;

        try {
            salonName = salonService.getCurrentSalon().getName();
            userId = SecurityUtil.getCurrentUserId();
            User u = userRepository.findById(userId).orElse(null);
            if (u != null) {
                isAdmin = u.getUserRoles().stream()
                    .anyMatch(r -> r.getRole().getName().contains("ADMIN"));
            }
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
                    "Service: " + next.getServices().stream().map(com.salon.entity.Service::getName).collect(java.util.stream.Collectors.joining(", ")) + "\n" +
                    "Time: " + next.getAppointmentTime() + "\n" +
                    "Status: " + next.getStatus()
            );
        }

        // Fetch all currently active services and products
        java.util.List<com.salon.entity.Service> allServices = serviceRepository.findBySalonNameAndActiveTrue(salonName);
        java.util.List<com.salon.entity.Product> allProducts = productRepository.findBySalonNameAndActiveTrue(salonName);

        // 👇 ADMIN ONLY COMMANDS
        if (isAdmin && (lower.contains("revenue") || lower.contains("dashboard") || lower.contains("summary") || lower.contains("report"))) {
            try {
                DashboardSummaryResponse summary = dashboardService.getSummary("week");
                return new BotResponse(
                    "Good day, Administrator.\nHere is your requested formal business summary for **" + salonName + "**:\n\n" +
                    "• **Total Revenue (Last 7 Days):** ₹" + summary.getTotalRevenue() + "\n" +
                    "• **New Customers:** " + summary.getNewCustomers() + "\n" +
                    "• **Today's Bookings:** " + summary.getBookingsToday() + "\n" +
                    "• **Completed Appointments:** " + summary.getCompletedBookings() + "\n\n" +
                    "Please refer to the Admin Dashboard for comprehensive analytics."
                );
            } catch (Exception e) {
                return new BotResponse("Good day, Administrator. I am currently unable to fetch the business summary. Please check the Dashboard directly.");
            }
        }

        // 👇 Specific item lookup: check if message contains any specific product/service name
        java.util.List<com.salon.entity.Service> matchedServices = allServices.stream()
                .filter(s -> lower.contains(s.getName().toLowerCase()))
                .collect(Collectors.toList());

        java.util.List<com.salon.entity.Product> matchedProducts = allProducts.stream()
                .filter(p -> lower.contains(p.getName().toLowerCase()))
                .collect(Collectors.toList());

        if (!matchedServices.isEmpty() || !matchedProducts.isEmpty()) {
            StringBuilder sb = new StringBuilder("Good day! I have located the specific items you requested:\n\n");
            for (com.salon.entity.Service s : matchedServices) {
                sb.append("✨ ").append(s.getName()).append(" - ₹").append(s.getPrice()).append(" (").append(s.getDurationInMinutes() != null ? s.getDurationInMinutes() : 60).append(" mins)\n");
            }
            for (com.salon.entity.Product p : matchedProducts) {
                sb.append("🛍️ ").append(p.getName()).append(" - ₹").append(p.getPrice())
                  .append(p.getStock() > 0 ? " (In Stock)" : " (Out of Stock)").append("\n");
            }
            sb.append("\nPlease let me know if you would like to proceed with a booking or purchase.");
            return new BotResponse(sb.toString());
        }

        // 👇 Generic lists queries
        if (lower.contains("service") || lower.contains("treatment")) {
            String servicesMsg = allServices.stream()
                    .map(s -> "• " + s.getName() + " (₹" + s.getPrice() + ")")
                    .collect(Collectors.joining("\n"));

            return new BotResponse(
                    servicesMsg.isEmpty()
                            ? "I apologize, but there are no active services available at this time."
                            : "Certainly. Here is our catalog of premium services:\n\n" + servicesMsg + "\n\nYou may formally request any of these through the 'Book Appointment' portal."
            );
        }

        if (lower.contains("product") || lower.contains("retail")) {
        	String products = allProducts.stream()
        	        .map(p -> "• " + p.getName() + " (₹" + p.getPrice() + ")")
        	        .collect(Collectors.joining("\n"));

            return new BotResponse(
                    products.isEmpty()
                            ? "I apologize, but our retail store is currently out of stock."
                            : "Certainly. Our premium retail products include:\n\n" + products + "\n\nThese are available for purchase directly through the Retail Store."
            );
        }

        if (lower.contains("book") || lower.contains("appointment")) {
            return new BotResponse(
                    "To secure your appointment, please navigate to the 'Book Appointment' section. This will allow you to select your preferred service, date, and exclusive appointment time."
            );
        }

        if (lower.contains("offer")) {
            return new BotResponse(
                    "At this moment, all our premium services are offered at their standard catalog price. Please monitor our homepage for future exclusive promotions."
            );
        }
        
        if (isAdmin && (lower.contains("help") || lower.contains("what can you do"))) {
            return new BotResponse(
                "Good day, Administrator. I am your Executive AI Assistant. I can assist you with:\n\n" +
                "- **Business Summary** -> e.g., 'What is the revenue?'\n" +
                "- **Service/Retail Lookup** -> e.g., 'Show me services'\n\n" +
                "How may I assist your management duties today?"
            );
        } else if (lower.contains("help") || lower.contains("what can you do")) {
            return new BotResponse(
                "Greetings! I am the Salon's Virtual Concierge. I can assist you with:\n\n" +
                "- **Premium Services** -> e.g., 'What services do you offer?'\n" +
                "- **Retail Products** -> e.g., 'Show me products'\n" +
                "- **Your Appointments** -> e.g., 'What is my booking status?'\n\n" +
                "How may I elevate your experience today?"
            );
        }

        return new BotResponse(
                "I apologize, but I am unable to comprehend your request.\n\n" +
                "Please type **'help'** to display the complete list of inquiries I am authorized to assist you with."
            );
        }
}

