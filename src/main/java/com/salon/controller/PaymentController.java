package com.salon.controller;

import com.salon.dto.CheckoutRequest;
import com.salon.dto.CheckoutResponse;
import com.salon.entity.Salon;
import com.salon.entity.SalonSubscription;
import com.salon.entity.SubscriptionPlan;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.SalonRepository;
import com.salon.repository.SalonSubscriptionRepository;
import com.salon.repository.SubscriptionPlanRepository;
import com.salon.service.SalonService;
import com.salon.service.StripePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Low-cost Stripe payment endpoints.
 * Admin subscribes salon to a plan; redirects to Stripe Checkout.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final record PlanResponse(Long id, String name, String description, Double monthlyPrice,
            Integer maxStaff, Integer maxCustomers, boolean loyaltyProgram, boolean advancedAnalytics) {}

    private final StripePaymentService stripeService;
    private final SalonService salonService;
    private final SubscriptionPlanRepository planRepository;
    private final SalonRepository salonRepository;
    private final SalonSubscriptionRepository subscriptionRepository;

    @GetMapping("/plans")
    public ResponseEntity<java.util.List<PlanResponse>> listPlans() {
        return ResponseEntity.ok(
                planRepository.findAll().stream()
                        .map(p -> new PlanResponse(p.getId(), p.getName(), p.getDescription(),
                                p.getMonthlyPrice(), p.getMaxStaff(), p.getMaxCustomers(),
                                p.isLoyaltyProgram(), p.isAdvancedAnalytics()))
                        .toList());
    }

    public PaymentController(StripePaymentService stripeService,
                             SalonService salonService,
                             SubscriptionPlanRepository planRepository,
                             SalonRepository salonRepository,
                             SalonSubscriptionRepository subscriptionRepository) {
        this.stripeService = stripeService;
        this.salonService = salonService;
        this.planRepository = planRepository;
        this.salonRepository = salonRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Create subscription checkout - returns URL to redirect user to Stripe.
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckoutResponse> createCheckout(@Valid @RequestBody CheckoutRequest request) {
        Salon salon = salonService.getCurrentSalon();
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", request.getPlanId()));

        String url = stripeService.createCheckoutSession(
                salon,
                plan,
                request.getSuccessUrl(),
                request.getCancelUrl()
        );

        return ResponseEntity.ok(new CheckoutResponse(url));
    }

    /**
     * Confirm payment after Stripe redirect. Call from frontend with session_id from URL.
     */
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> confirmPayment(@RequestParam String session_id) {
        var result = stripeService.confirmCheckoutSession(session_id);

        Salon salon = salonRepository.findByNameIgnoreCase(result.salonName())
                .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", result.salonName()));
        SubscriptionPlan plan = planRepository.findById(result.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", result.planId()));

        SalonSubscription sub = new SalonSubscription();
        sub.setSalonName(salon.getName());
        sub.setPlan(plan);
        sub.setStripeCustomerId(result.stripeCustomerId());
        sub.setStripeSubscriptionId(result.stripeSubscriptionId());
        sub.setActive(true);
        subscriptionRepository.save(sub);

        return ResponseEntity.ok("Subscription activated");
    }
}
