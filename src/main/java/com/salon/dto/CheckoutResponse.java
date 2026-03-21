package com.salon.dto;

/** Redirect URL to Stripe Checkout - user pays there, no card data on your server */
public record CheckoutResponse(String checkoutUrl) {}
