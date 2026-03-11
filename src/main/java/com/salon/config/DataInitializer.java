package com.salon.config;

import com.salon.entity.ApprovalStatus;
import com.salon.entity.Role;
import com.salon.entity.User;
import com.salon.entity.UserRole;
import com.salon.repository.RoleRepository;
import com.salon.repository.UserRepository;
import com.salon.repository.UserRoleRepository;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    
    @Bean
    CommandLineRunner initSuperAdmin(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserRoleRepository userRoleRepository) {
    	

        return args -> {

            if (userRepository.findByEmail("systemadmin@gmail.com").isPresent()) {
                return;
            }

            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseThrow();

            User superAdmin = new User();
            superAdmin.setFullName("System Admin");
            superAdmin.setEmail("systemadmin@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123")); // ✅ REAL PASSWORD
            superAdmin.setActive(true);
            superAdmin.setApprovalStatus(ApprovalStatus.APPROVED);
            superAdmin.setCreatedAt(LocalDateTime.now());

            userRepository.save(superAdmin);

            userRoleRepository.save(new UserRole(superAdmin, superAdminRole));
            
            if (roleRepository.findByName("ROLE_CUSTOMER").isEmpty()) {
                roleRepository.save(new Role("ROLE_CUSTOMER"));
            }

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role("ROLE_ADMIN"));
            }

            if (roleRepository.findByName("ROLE_SALON_OWNER").isEmpty()) {
                roleRepository.save(new Role("ROLE_SALON_OWNER"));
            }
        };
    }
}
