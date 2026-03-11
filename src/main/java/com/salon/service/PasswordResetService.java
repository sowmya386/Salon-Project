package com.salon.service;

import com.salon.entity.PasswordResetToken;
import com.salon.entity.User;
import com.salon.repository.PasswordResetTokenRepository;
import com.salon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepo,
                                UserRepository userRepo,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // 1) Forgot password
 // 1️⃣ Forgot password (SaaS-safe)
    public void createResetToken(String email) {

        userRepo.findByEmail(email).ifPresent(user -> {

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

            PasswordResetToken resetToken =
                    new PasswordResetToken(token, user, expiry);

            tokenRepo.save(resetToken);

            // DEV MODE: print instead of email
            System.out.println("RESET PASSWORD TOKEN:");
            System.out.println(
                "http://localhost:3000/reset-password?token=" + token
            );
        });

        // ✅ IMPORTANT
        // Do NOTHING if user not found
        // Prevents email enumeration attacks
    }


    // 2) Reset password
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetToken.setUsed(true);
        tokenRepo.save(resetToken);
    }
}
