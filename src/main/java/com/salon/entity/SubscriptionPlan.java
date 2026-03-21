package com.salon.entity;

import jakarta.persistence.*;

/**
 * SaaS subscription plan - drives $5K+ revenue.
 * Basic $49, Pro $99, Enterprise $199/month.
 */
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // BASIC, PRO, ENTERPRISE

    private String description;

    @Column(nullable = false)
    private Double monthlyPrice;

    private Integer maxStaff = 5;
    private Integer maxCustomers = 500;
    private boolean loyaltyProgram = false;
    private boolean advancedAnalytics = false;
    private boolean smsNotifications = false;
    private boolean apiAccess = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(Double monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public Integer getMaxStaff() { return maxStaff; }
    public void setMaxStaff(Integer maxStaff) { this.maxStaff = maxStaff; }
    public Integer getMaxCustomers() { return maxCustomers; }
    public void setMaxCustomers(Integer maxCustomers) { this.maxCustomers = maxCustomers; }
    public boolean isLoyaltyProgram() { return loyaltyProgram; }
    public void setLoyaltyProgram(boolean loyaltyProgram) { this.loyaltyProgram = loyaltyProgram; }
    public boolean isAdvancedAnalytics() { return advancedAnalytics; }
    public void setAdvancedAnalytics(boolean advancedAnalytics) { this.advancedAnalytics = advancedAnalytics; }
    public boolean isSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }
    public boolean isApiAccess() { return apiAccess; }
    public void setApiAccess(boolean apiAccess) { this.apiAccess = apiAccess; }
}
