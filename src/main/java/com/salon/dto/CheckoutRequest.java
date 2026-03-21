package com.salon.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "Plan ID is required")
    private Long planId;

    private String successUrl;
    private String cancelUrl;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
