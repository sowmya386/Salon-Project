package com.salon.controller;

import com.salon.dto.*;
import com.salon.entity.User;
import com.salon.security.JwtUtil;
import com.salon.service.PasswordResetService;
import com.salon.service.SupabaseJwtVerifier;
import com.salon.service.UserService;
import com.salon.repository.SalonRepository;
import com.salon.entity.Salon;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;
    private final SupabaseJwtVerifier supabaseJwtVerifier;
    private final SalonRepository salonRepository;

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          PasswordResetService passwordResetService,
                          SupabaseJwtVerifier supabaseJwtVerifier,
                          SalonRepository salonRepository) {

        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
        this.supabaseJwtVerifier = supabaseJwtVerifier;
        this.salonRepository = salonRepository;
    }

    // ================= ADMIN REGISTRATION =================
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request) {

        userService.registerAdmin(request);
        return ResponseEntity.ok("Admin registered successfully");
    }
 // ================= CUSTOMER REGISTRATION =================
    @PostMapping("/customers/register")
    public ResponseEntity<?> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request) {
        userService.registerCustomerSelf(request); // ✅ method exists in UserService
        return ResponseEntity.ok("Customer registered successfully");
    }

    // ================= SPECIFIC POSTMAN LOGIN ENDPOINTS =================
    @PostMapping("/super-admin/login")
    public ResponseEntity<LoginResponse> superAdminLogin(@Valid @RequestBody LoginRequest request) {
        return login(request);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return login(request);
    }

    @PostMapping("/customers/login")
    public ResponseEntity<LoginResponse> customerLogin(@Valid @RequestBody LoginRequest request) {
        return login(request);
    }

    // ================= UNIFIED LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.loginUnified(
                request.getEmail(),
                request.getPassword()
        );

        List<String> roles = user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getSalonName(),
                roles
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }



    // ================= SUPABASE / GOOGLE OAUTH =================
    /**
     * Exchange Supabase access token (from Google OAuth sign-in) for salon JWT.
     * Frontend: supabase.auth.signInWithOAuth({ provider: 'google' }) → session.access_token
     * Send access_token here with optional salonName (required for new customers).
     */
    @PostMapping("/supabase/exchange")
    public ResponseEntity<LoginResponse> supabaseExchange(
            @Valid @RequestBody SupabaseExchangeRequest request) {

        var info = supabaseJwtVerifier.verifyAndExtract(request.getAccessToken());

        User user = userService.findOrCreateFromSupabase(
                info,
                request.getSalonName(),
                request.getRole() != null ? request.getRole() : "ROLE_CUSTOMER"
        );

        // Admin approval check (same as email/password login)
        boolean isAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_ADMIN".equals(ur.getRole().getName()));
        if (isAdmin) {
            if (user.getApprovalStatus() != com.salon.entity.ApprovalStatus.APPROVED) {
                throw new RuntimeException("Admin account not approved yet");
            }
            if (user.getSalonName() != null) {
                Salon salon = salonRepository.findByNameIgnoreCase(user.getSalonName()).orElseThrow();
                if (salon.getApprovalStatus() != com.salon.entity.ApprovalStatus.APPROVED) {
                    throw new RuntimeException("Salon not approved yet");
                }
            }
        }

        List<String> roles = user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        String salonName = user.getSalonName();

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                salonName,
                roles
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
    		@Valid  @RequestBody ForgotPasswordRequest request) {

        passwordResetService.createResetToken(request.getEmail());
        return ResponseEntity.ok(
                "If the email exists, a reset link has been sent"
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
    		@Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Password reset successful");
    }
}
