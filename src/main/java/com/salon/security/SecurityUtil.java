package com.salon.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // ================= CURRENT USER =================
//    public static Long getCurrentUserId() {
//
//        Authentication auth =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || auth.getPrincipal() == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//
//        return (Long) auth.getPrincipal(); // ✅ userId
//    }
	public static Long getCurrentUserId() {

	    Authentication auth =
	            SecurityContextHolder.getContext().getAuthentication();

	    if (auth == null || auth.getPrincipal() == null) {
	        throw new RuntimeException("User not authenticated");
	    }

	   
	    return (Long) auth.getPrincipal();
	}

    // ================= CURRENT SALON (single-tenant, by name) =================
    public static String getCurrentSalonName() {
        String salonName = SalonContext.getSalonName();
        if (salonName == null || salonName.isBlank()) {
            throw new RuntimeException("Salon name not found in context");
        }
        return salonName;
    }
}
