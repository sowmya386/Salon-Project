package com.salon.service;

import com.salon.dto.AdminRegisterRequest;
import com.salon.dto.CustomerRegisterRequest;
import com.salon.entity.*;
import com.salon.exception.EmailAlreadyExistsException;
import com.salon.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SalonRepository salonRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       SalonRepository salonRepository,
                       UserRoleRepository userRoleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.salonRepository = salonRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= CUSTOMER SELF REGISTRATION =================
    public User registerCustomerSelf(CustomerRegisterRequest request) {

        Salon salon = salonRepository
                .findByNameIgnoreCase(request.getSalonName())
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        if (userRepository.existsByEmailAndSalonId(
                request.getEmail(), salon.getId())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists in this salon"
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalon(salon);
        customer.setActive(true);

        customer = userRepository.save(customer);

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        userRoleRepository.save(new UserRole(customer, role));

        return customer;
    }

    // ================= ADMIN REGISTERS CUSTOMER =================
    public User registerCustomerByAdmin(CustomerRegisterRequest request, Long salonId) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        if (userRepository.existsByEmailAndSalonId(
                request.getEmail(), salon.getId())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists in this salon"
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalon(salon);
        customer.setActive(true);

        customer = userRepository.save(customer);

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        userRoleRepository.save(new UserRole(customer, role));

        return customer;
    }

    // ================= ADMIN REGISTRATION =================
    public User registerAdmin(AdminRegisterRequest request) {

        // 1️⃣ Create salon (PENDING)
        Salon salon = new Salon();
        salon.setName(request.getSalonName());
        salon.setApprovalStatus(ApprovalStatus.PENDING);
        salon = salonRepository.save(salon);

        // 2️⃣ Check email uniqueness inside salon
        if (userRepository.existsByEmailAndSalonId(
                request.getEmail(), salon.getId())) {
            throw new RuntimeException("Email already exists for this salon");
        }

        // 3️⃣ Create admin (PENDING)
        User admin = new User();
        admin.setFullName(request.getFullName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setActive(true);
        admin.setSalon(salon);
        admin.setApprovalStatus(ApprovalStatus.PENDING);

        admin = userRepository.save(admin);

        // 4️⃣ Assign ROLE_ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        userRoleRepository.save(new UserRole(admin, adminRole));

        return admin;
    }

    // ================= LOGIN =================
    public User loginCustomer(String email, String password, String salonName) {

    	 Salon salon = salonRepository.findByNameIgnoreCase(salonName)
    	            .orElseThrow(() -> new RuntimeException("Salon not found"));

    	    User user = userRepository.findByEmailAndSalon(email, salon)
    	            .orElseThrow(() -> new RuntimeException("User not found"));


        boolean isCustomer = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_CUSTOMER"));

        if (!isCustomer) {
            throw new RuntimeException("Not a customer account");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }


    public User loginAdmin(String email, String password, String salonName) {

    	 Salon salon = salonRepository.findByNameIgnoreCase(salonName)
    	            .orElseThrow(() -> new RuntimeException("Salon not found"));

    	    User user = userRepository.findByEmailAndSalon(email, salon)
    	            .orElseThrow(() -> new RuntimeException("User not found"));


//        boolean isAdmin = user.getUserRoles().stream()
//                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_ADMIN"));
//
//        if (!isAdmin) {
//            throw new RuntimeException("Not an admin account");
//        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Admin account not approved yet");
        }

        if (user.getSalon().getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Salon not approved yet");
        }


        return user;
    }
    
    public User loginSuperAdmin(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        boolean isSuperAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            throw new RuntimeException("Not a super admin");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Super admin not approved");
        }

        return user;
    }



    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
