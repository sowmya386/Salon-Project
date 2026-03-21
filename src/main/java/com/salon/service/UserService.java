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
        String salonName = (request.getSalonName() != null && !request.getSalonName().isBlank())
                ? request.getSalonName() : defaultSalonName;

        Salon salon = salonRepository
                .findByNameIgnoreCase(salonName)
                .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", salonName));

        if (userRepository.existsByEmailAndSalonName(
                request.getEmail(), salon.getName())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists in this salon"
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalonName(salon.getName());
        customer.setActive(true);

        customer = userRepository.save(customer);

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER"));

        userRoleRepository.save(new UserRole(customer, role));

        return customer;
    }

    // ================= ADMIN REGISTERS CUSTOMER =================
    public User registerCustomerByAdmin(CustomerRegisterRequest request, Salon salon) {

        if (userRepository.existsByEmailAndSalonName(
                request.getEmail(), salon.getName())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists in this salon"
            );
        }

        User customer = new User();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setSalonName(salon.getName());
        customer.setActive(true);

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

        // 2️⃣ Check email uniqueness inside salon
        if (userRepository.existsByEmailAndSalonName(
                request.getEmail(), salon.getName())) {
            throw new RuntimeException("Email already exists for this salon");
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
    public User loginCustomer(String email, String password, String salonName) {
        String name = (salonName != null && !salonName.isBlank()) ? salonName : defaultSalonName;

    	 Salon salon = salonRepository.findByNameIgnoreCase(name)
    	            .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", name));

    	    User user = userRepository.findByEmailAndSalonName(email, salon.getName())
    	            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));


        boolean isCustomer = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_CUSTOMER"));

        if (!isCustomer) {
            throw new BadRequestException("Not a customer account");
        }

        if (user.getPassword() == null) {
            throw new BadRequestException("This account uses Google sign-in. Please sign in with Google.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return user;
    }


    public User loginAdmin(String email, String password, String salonName) {
        String name = (salonName != null && !salonName.isBlank()) ? salonName : defaultSalonName;

    	 Salon salon = salonRepository.findByNameIgnoreCase(name)
    	            .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", name));

    	    User user = userRepository.findByEmailAndSalonName(email, salon.getName())
    	            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));


//        boolean isAdmin = user.getUserRoles().stream()
//                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_ADMIN"));
//
//        if (!isAdmin) {
//            throw new RuntimeException("Not an admin account");
//        }

        if (user.getPassword() == null) {
            throw new BadRequestException("This account uses Google sign-in. Please sign in with Google.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Admin account not approved yet");
        }

        if (salon.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Salon not approved yet");
        }


        return user;
    }
    
    public User loginSuperAdmin(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        boolean isSuperAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            throw new RuntimeException("Not a super admin");
        }

        if (user.getPassword() == null) {
            throw new BadRequestException("This account uses Google sign-in. Please sign in with Google.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Super admin not approved");
        }

        return user;
    }



    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User findOrCreateFromSupabase(SupabaseUserInfo info, String salonName, String roleName) {
        String name = (salonName != null && !salonName.isBlank()) ? salonName : defaultSalonName;

        Salon salon = salonRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Salon", "name", name));

        // Find by email and salonName
        var existingByEmail = userRepository.findByEmailAndSalonName(info.email(), salon.getName());
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
        }

        user = userRepository.save(user);

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("ROLE_CUSTOMER")
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_CUSTOMER")));

        userRoleRepository.save(new UserRole(user, role));

        return user;
    }
}
