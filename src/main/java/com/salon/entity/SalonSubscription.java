package com.salon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Links salon to subscription plan - revenue tracking.
 */
@Entity
@Table(name = "salon_subscriptions", indexes = {
    @Index(name = "idx_salon_sub_salon", columnList = "salon_name")
})
public class SalonSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salon_name", nullable = false)
    private String salonName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate;
    private String stripeCustomerId;  // For Stripe integration
    private String stripeSubscriptionId;
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSalonName() { return salonName; }
    public void setSalonName(String salonName) { this.salonName = salonName; }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }
    public String getStripeSubscriptionId() { return stripeSubscriptionId; }
    public void setStripeSubscriptionId(String stripeSubscriptionId) { this.stripeSubscriptionId = stripeSubscriptionId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
