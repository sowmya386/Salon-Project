package com.salon.controller;

import com.salon.dto.*;
import com.salon.entity.User;
import com.salon.security.JwtUtil;
import com.salon.service.PasswordResetService;
import com.salon.service.UserService;

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

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          PasswordResetService passwordResetService) {

        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    // ================= ADMIN REGISTRATION =================
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request) {

        userService.registerAdmin(request);
        return ResponseEntity.ok("Admin registered successfully");
    }


    // ================= ADMIN LOGIN =================
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(
    		@Valid @RequestBody LoginRequest request) {

        User admin = userService.loginAdmin(
                request.getEmail(),
                request.getPassword(),
                request.getSalonName()
        );

        List<String> roles = admin.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        String token = jwtUtil.generateToken(
                admin.getId(),
                admin.getEmail(),
                admin.getSalon().getId(),
                roles
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    // ================= CUSTOMER LOGIN =================
    @PostMapping("/customers/login")
    public ResponseEntity<LoginResponse> customerLogin(
    		@Valid  @RequestBody LoginRequest request) {

    	User customer = userService.loginCustomer(
    		    request.getEmail(),
    		    request.getPassword(),
    		    request.getSalonName()
    		);


        List<String> roles = customer.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        String token = jwtUtil.generateToken(
                customer.getId(),
                customer.getEmail(),
                customer.getSalon().getId(),
                roles
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @PostMapping("/super-admin/login")
    public ResponseEntity<LoginResponse> superAdminLogin(
            @Valid @RequestBody SuperAdminLoginRequest request) {

        User superAdmin = userService.loginSuperAdmin(
                request.getEmail(),
                request.getPassword()
        );

        List<String> roles = superAdmin.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        String token = jwtUtil.generateToken(
                superAdmin.getId(),
                superAdmin.getEmail(),
                null,
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
