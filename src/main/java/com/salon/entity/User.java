package com.salon.entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_salon_name", columnList = "salon_name"),
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_provider_id", columnList = "provider_id")
})
public class User {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String fullName;

	    @Column(nullable = false)
	    private String email;

    @Column
    private String phone;

    @Column
    private String password;  // nullable for OAuth-only users (e.g. Google sign-in)

    /** Supabase/auth provider user ID (e.g. UUID from Supabase) */
    @Column
    private String providerId;

    /** Auth provider: "email", "google", etc. */
    @Column
    private String authProvider = "email";

	    @Column(nullable = false)
	    private boolean active = true;

	    @Column(nullable = false)
	    private LocalDateTime createdAt = LocalDateTime.now();

	    @Column(name = "salon_name", nullable = true)
	    private String salonName;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    private List<UserRole> userRoles = new ArrayList<>();

	    /** Loyalty points (customers only) - 1 point per $1 spent, redeemable */
	    @Column
	    private Integer loyaltyPoints = 0;

        @Column(columnDefinition = "TEXT")
        private String homeAddress;

        @Column(columnDefinition = "TEXT")
        private String profileImageUrl;

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    
   

    // REQUIRED by JPA
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }
    
    public List<UserRole> getUserRoles() {
        return userRoles;
    }

	public void setCreatedAt(LocalDateTime now) {
		this.createdAt = LocalDateTime.now();
	}

	public Integer getLoyaltyPoints() { return loyaltyPoints; }
	public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
