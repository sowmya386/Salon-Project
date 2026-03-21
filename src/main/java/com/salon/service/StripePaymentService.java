package com.salon.service;

import com.salon.entity.Salon;
import com.salon.entity.SubscriptionPlan;
import com.salon.exception.BadRequestException;
import com.salon.repository.SalonSubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Stripe payment integration - low cost: 2.9% + $0.30 per transaction.
 * No monthly fees. Configure stripe.secret-key and stripe.price-ids in properties.
 */
@Service
public class StripePaymentService {

    private final SalonSubscriptionRepository subscriptionRepository;

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    /** Stripe Price IDs for each plan (create in Stripe Dashboard) */
    @Value("${stripe.price-id.basic:}")
    private String priceIdBasic;

    @Value("${stripe.price-id.pro:}")
    private String priceIdPro;

    @Value("${stripe.price-id.enterprise:}")
    private String priceIdEnterprise;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public StripePaymentService(SalonSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    private void ensureConfigured() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new BadRequestException(
                    "Stripe is not configured. Set stripe.secret-key in application.properties. " +
                    "Get keys from https://dashboard.stripe.com/apikeys (free account)");
        }
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Create Stripe Checkout session for subscription.
     * Redirects user to Stripe-hosted payment page (PCI compliant, no card data on your server).
     */
    public String createCheckoutSession(Salon salon, SubscriptionPlan plan, String successUrl, String cancelUrl) {
        ensureConfigured();

        String priceId = getPriceIdForPlan(plan.getName());
        if (priceId == null || priceId.isBlank()) {
            throw new BadRequestException(
                    "Stripe price not configured for plan: " + plan.getName() +
                    ". Set stripe.price-id." + plan.getName().toLowerCase() + " in properties.");
        }

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setSuccessUrl(successUrl != null ? successUrl : frontendUrl + "/subscription/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl != null ? cancelUrl : frontendUrl + "/subscription/cancel")
                    .putMetadata("salonName", salon.getName())
                    .putMetadata("planId", plan.getId().toString())
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new BadRequestException("Payment setup failed: " + e.getMessage());
        }
    }

    /**
     * Create one-time payment (e.g. invoice, top-up).
     * Amount in smallest currency unit (cents for USD).
     */
    public String createOneTimePayment(long amountCents, String description, String successUrl, String cancelUrl, String metadata) {
        ensureConfigured();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(description != null ? description : "Payment")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setSuccessUrl(successUrl != null ? successUrl : frontendUrl + "/payment/success")
                    .setCancelUrl(cancelUrl != null ? cancelUrl : frontendUrl + "/payment/cancel")
                    .putMetadata("custom", metadata != null ? metadata : "")
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new BadRequestException("Payment setup failed: " + e.getMessage());
        }
    }

    /**
     * Confirm payment after redirect - retrieves session from Stripe, activates subscription.
     * Returns salonId and planId from session metadata for caller to load entities.
     */
    public ConfirmResult confirmCheckoutSession(String sessionId) {
        ensureConfigured();

        try {
            Session session = Session.retrieve(sessionId);

            if (!"complete".equals(session.getPaymentStatus()) && !"paid".equals(session.getPaymentStatus())) {
                throw new BadRequestException("Payment not completed");
            }

            String salonName = session.getMetadata().get("salonName");
            String planIdStr = session.getMetadata().get("planId");
            if (salonName == null || planIdStr == null) {
                throw new BadRequestException("Invalid session metadata");
            }

            return new ConfirmResult(
                    salonName,
                    Long.parseLong(planIdStr),
                    session.getCustomer(),
                    session.getSubscription()
            );
        } catch (StripeException e) {
            throw new BadRequestException("Could not confirm payment: " + e.getMessage());
        }
    }

    public record ConfirmResult(String salonName, long planId, String stripeCustomerId, String stripeSubscriptionId) {}

    private String getPriceIdForPlan(String planName) {
        return switch (planName.toUpperCase()) {
            case "BASIC" -> priceIdBasic;
            case "PRO" -> priceIdPro;
            case "ENTERPRISE" -> priceIdEnterprise;
            default -> null;
        };
    }
}
