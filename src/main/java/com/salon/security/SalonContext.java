package com.salon.security;

public class SalonContext {

    private static final ThreadLocal<Long> SALON_ID = new ThreadLocal<>();

    public static void setSalonId(Long salonId) {
        SALON_ID.set(salonId);
    }

    public static Long getSalonId() {
        return SALON_ID.get();
    }

    public static void clear() {
        SALON_ID.remove();
    }
}