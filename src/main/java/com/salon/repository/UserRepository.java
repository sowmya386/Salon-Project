package com.salon.repository;

import com.salon.entity.Salon;
import com.salon.entity.User;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔐 LOGIN (FINAL)
	Optional<User> findByEmailAndSalonName(String email, String salonName);

    // 🔐 REGISTRATION VALIDATION
    boolean existsByEmailAndSalonName(String email, String salonName);

    // 🔧 UTIL
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    // 👑 SUPER ADMIN APPROVAL
    @Modifying
    @Transactional
    @Query("""
        UPDATE User u
        SET u.approvalStatus = 'APPROVED'
        WHERE u.salonName = :salonName
          AND EXISTS (
              SELECT ur FROM UserRole ur
              WHERE ur.user = u AND ur.role.name = 'ROLE_ADMIN'
          )
    """)
    void approveAdminsBySalonName(@org.springframework.data.repository.query.Param("salonName") String salonName);
}
