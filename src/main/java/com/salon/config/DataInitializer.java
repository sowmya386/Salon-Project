package com.salon.config;

import com.salon.entity.ApprovalStatus;
import com.salon.entity.Role;
import com.salon.entity.Salon;
import com.salon.entity.SubscriptionPlan;
import com.salon.entity.User;
import com.salon.entity.UserRole;
import com.salon.repository.RoleRepository;
import com.salon.repository.SalonRepository;
import com.salon.repository.SubscriptionPlanRepository;
import com.salon.repository.UserRepository;
import com.salon.repository.UserRoleRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${salon.default-name:Default}")
    private String defaultSalonName;

    @Bean
    CommandLineRunner initDefaultSalon(SalonRepository salonRepository) {
        return args -> {
            if (salonRepository.findByNameIgnoreCase(defaultSalonName).isEmpty()) {
                Salon salon = new Salon();
                salon.setName(defaultSalonName);
                salon.setApprovalStatus(ApprovalStatus.APPROVED);
                salon.setActive(true);
                salonRepository.save(salon);
            }
        };
    }

    @Bean
    CommandLineRunner initSubscriptionPlans(SubscriptionPlanRepository planRepository) {
        return args -> {
            if (planRepository.findByNameIgnoreCase("BASIC").isEmpty()) {
                SubscriptionPlan basic = new SubscriptionPlan();
                basic.setName("BASIC");
                basic.setDescription("Essential for small salons");
                basic.setMonthlyPrice(49.0);
                basic.setMaxStaff(3);
                basic.setMaxCustomers(200);
                planRepository.save(basic);

                SubscriptionPlan pro = new SubscriptionPlan();
                pro.setName("PRO");
                pro.setDescription("For growing salons");
                pro.setMonthlyPrice(99.0);
                pro.setMaxStaff(10);
                pro.setMaxCustomers(1000);
                pro.setLoyaltyProgram(true);
                pro.setAdvancedAnalytics(true);
                planRepository.save(pro);

                SubscriptionPlan enterprise = new SubscriptionPlan();
                enterprise.setName("ENTERPRISE");
                enterprise.setDescription("Full features");
                enterprise.setMonthlyPrice(199.0);
                enterprise.setMaxStaff(50);
                enterprise.setMaxCustomers(10000);
                enterprise.setLoyaltyProgram(true);
                enterprise.setAdvancedAnalytics(true);
                enterprise.setSmsNotifications(true);
                enterprise.setApiAccess(true);
                planRepository.save(enterprise);
            }
        };
    }

    @Bean
    CommandLineRunner initSuperAdmin(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserRoleRepository userRoleRepository) {

        return args -> {

            // ✅ Create all roles first if they don't exist
            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN")));

            if (roleRepository.findByName("ROLE_CUSTOMER").isEmpty()) {
                roleRepository.save(new Role("ROLE_CUSTOMER"));
            }
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role("ROLE_ADMIN"));
            }
            if (roleRepository.findByName("ROLE_SALON_OWNER").isEmpty()) {
                roleRepository.save(new Role("ROLE_SALON_OWNER"));
            }

            // ✅ Ensure super admin exists and forcefully reset password to admin123
            User superAdmin = userRepository.findFirstByEmail("systemadmin@gmail.com")
                    .orElseGet(User::new);

            superAdmin.setFullName("System Admin");
            superAdmin.setEmail("systemadmin@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123")); // Forces correct hash
            superAdmin.setActive(true);
            superAdmin.setApprovalStatus(ApprovalStatus.APPROVED);
            
            if (superAdmin.getCreatedAt() == null) {
                superAdmin.setCreatedAt(LocalDateTime.now());
            }
            
            userRepository.save(superAdmin);

            // Ensure super admin has the role mapped
            boolean hasRole = userRoleRepository.findByUser(superAdmin).stream()
                    .anyMatch(ur -> ur.getRole().getName().equals("ROLE_SUPER_ADMIN"));

            if (!hasRole) {
                userRoleRepository.save(new UserRole(superAdmin, superAdminRole));
            }
        };
    }
}