package com.salon.security;

/**
 * ThreadLocal context for single-tenant mode.
 * Stores salon name (not ID) as the tenant identifier.
 */
public class SalonContext {

    private static final ThreadLocal<String> SALON_NAME = new ThreadLocal<>();

    public static void setSalonName(String salonName) {
        SALON_NAME.set(salonName);
    }

    public static String getSalonName() {
        return SALON_NAME.get();
    }

    public static void clear() {
        SALON_NAME.remove();
    }
}