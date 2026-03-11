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

    // ================= CURRENT SALON =================
    public static Long getCurrentSalonId() {

        Long salonId = SalonContext.getSalonId();

        if (salonId == null) {
            throw new RuntimeException("SalonId not found in context");
        }

        return salonId;
    }

    // ================= DO NOT USE =================
    public static void setCurrentUser(Long userId, Long salonId) {
        // intentionally empty
        // context is set ONLY in JwtAuthFilter
    }
}
