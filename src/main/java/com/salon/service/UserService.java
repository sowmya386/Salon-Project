package com.salon.service;

import com.salon.dto.AdminRegisterRequest;
import com.salon.dto.CustomerRegisterRequest;
import com.salon.entity.*;
import com.salon.exception.BadRequestException;
import com.salon.exception.ConflictException;
import com.salon.exception.EmailAlreadyExistsException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.exception.UnauthorizedException;
import com.salon.repository.*;
import com.salon.service.SupabaseJwtVerifier.SupabaseUserInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${salon.default-name:Default}")
    private String defaultSalonName;

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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists. Please log in or use a different email."
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalonName(null); // Customers are global
        customer.setActive(true);
        customer.setApprovalStatus(ApprovalStatus.APPROVED);

        customer = userRepository.save(customer);

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER"));

        userRoleRepository.save(new UserRole(customer, role));

        return customer;
    }

    // ================= ADMIN REGISTERS CUSTOMER =================
    public User registerCustomerByAdmin(CustomerRegisterRequest request, Salon salon) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists. Please log in or use a different email."
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalonName(salon.getName());
        customer.setActive(true);
        customer.setApprovalStatus(ApprovalStatus.APPROVED);

        customer = userRepository.save(customer);

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER"));

        userRoleRepository.save(new UserRole(customer, role));

        return customer;
    }

    // ================= ADMIN REGISTRATION =================
    public User registerAdmin(AdminRegisterRequest request) {
        // Single-tenant: use default salon name
        String salonName = (request.getSalonName() != null && !request.getSalonName().isBlank())
                ? request.getSalonName() : defaultSalonName;

        // 1️⃣ Get or create the (single) salon
        Salon salon = salonRepository.findByNameIgnoreCase(salonName)
                .orElseGet(() -> {
                    Salon newSalon = new Salon();
                    newSalon.setName(salonName);
                    newSalon.setApprovalStatus(ApprovalStatus.PENDING);
                    return salonRepository.save(newSalon);
                });

        // 2️⃣ Check email uniqueness system-wide
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists. Please log in or use a different email.");
        }

        // 3️⃣ Create admin (PENDING)
        User admin = new User();
        admin.setFullName(request.getFullName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setActive(true);
        admin.setSalonName(salon.getName());
        admin.setApprovalStatus(ApprovalStatus.PENDING);

        admin = userRepository.save(admin);

        // 4️⃣ Assign ROLE_ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_ADMIN"));

        userRoleRepository.save(new UserRole(admin, adminRole));

        return admin;
    }

    // ================= LOGIN =================
    // ================= UNIFIED LOGIN =================
    public User loginUnified(String email, String password) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.getPassword() == null) {
            throw new BadRequestException("This account uses Google sign-in. Please sign in with Google.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        boolean isSuperAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_SUPER_ADMIN"));
        boolean isAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_ADMIN"));

        if (isSuperAdmin) {
            if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
                throw new BadRequestException("Super admin not approved");
            }
        } else if (isAdmin) {
            if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
                throw new BadRequestException("Admin account not approved yet");
            }
            if (user.getSalonName() != null) {
                Salon salon = salonRepository.findByNameIgnoreCase(user.getSalonName())
                        .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", user.getSalonName()));
                if (salon.getApprovalStatus() != ApprovalStatus.APPROVED) {
                    throw new BadRequestException("Salon not approved yet");
                }
            }
        }

        return user;
    }



    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User findOrCreateFromSupabase(SupabaseUserInfo info, String salonName, String roleName) {
        String name = (salonName != null && !salonName.isBlank()) ? salonName : defaultSalonName;

        // 1. Get or create the salon if Admin, else just find it
        Salon salon = salonRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    if ("ROLE_ADMIN".equals(roleName)) {
                        Salon newSalon = new Salon();
                        newSalon.setName(name);
                        newSalon.setApprovalStatus(ApprovalStatus.PENDING);
                        return salonRepository.save(newSalon);
                    }
                    throw new ResourceNotFoundException("Salon", "name", name);
                });

        // Find by global email
        var existingByEmail = userRepository.findFirstByEmail(info.email());
        if (existingByEmail.isPresent()) {
            User user = existingByEmail.get();
            // Link OAuth provider if not already linked
            if (user.getProviderId() == null || !user.getProviderId().equals(info.providerId())) {
                user.setProviderId(info.providerId());
                user.setAuthProvider("google");
                user = userRepository.save(user);
            }
            return user;
        }

        // New user
        User user = new User();
        user.setFullName(info.fullName());
        user.setEmail(info.email());
        user.setPassword(null);  // OAuth-only, no password
        user.setProviderId(info.providerId());
        user.setAuthProvider("google");
        user.setSalonName(salon.getName());
        user.setActive(true);

        if ("ROLE_ADMIN".equals(roleName)) {
            user.setApprovalStatus(ApprovalStatus.PENDING);
        } else {
            user.setApprovalStatus(ApprovalStatus.APPROVED);
        }

        user = userRepository.save(user);

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("ROLE_CUSTOMER")
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER")));

        userRoleRepository.save(new UserRole(user, role));

        return user;
    }

    public org.springframework.data.domain.Page<com.salon.dto.CustomerProfileResponse> getSalonCustomers(org.springframework.data.domain.Pageable pageable) {
        String salonName = com.salon.security.SecurityUtil.getCurrentSalonName();
        return userRepository.findCustomersBySalonName(salonName, pageable)
                .map(c -> new com.salon.dto.CustomerProfileResponse(
                        c.getId(), c.getFullName(), c.getEmail(), c.getPhone(), c.getSalonName(), c.getHomeAddress(), c.getPincode(), c.getProfileImageUrl()
                ));
    }

    public User updateCustomerProfile(Long userId, com.salon.dto.CustomerProfileUpdateRequest request) {
        User customer = getUserById(userId);
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setHomeAddress(request.getHomeAddress());
        customer.setPincode(request.getPincode());
        customer.setProfileImageUrl(request.getProfileImageUrl());
        return userRepository.save(customer);
    }

    public java.util.List<com.salon.dto.CustomerProfileResponse> getPendingAdminsForSalon() {
        String salonName = com.salon.security.SecurityUtil.getCurrentSalonName();
        return userRepository.findPendingAdminsBySalonName(salonName)
            .stream()
            .map(u -> new com.salon.dto.CustomerProfileResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getSalonName(), null, null, null))
            .collect(java.util.stream.Collectors.toList());
    }

    public void approvePendingAdminLocally(Long targetUserId) {
        String currentSalonName = com.salon.security.SecurityUtil.getCurrentSalonName();
        User pendingUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new com.salon.exception.ResourceNotFoundException("User", "id", targetUserId.toString()));

        if (!currentSalonName.equalsIgnoreCase(pendingUser.getSalonName())) {
            throw new com.salon.exception.ForbiddenException("You can only approve admins for your own salon.");
        }

        boolean isTargetAdmin = pendingUser.getUserRoles().stream()
            .anyMatch(ur -> ur.getRole().getName().equals("ROLE_ADMIN"));
        
        if (!isTargetAdmin) {
            throw new com.salon.exception.BadRequestException("Target user is not registered as an Admin.");
        }

        pendingUser.setApprovalStatus(com.salon.entity.ApprovalStatus.APPROVED);
        userRepository.save(pendingUser);
    }
}
