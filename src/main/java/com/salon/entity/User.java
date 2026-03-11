package com.salon.entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "users")

public class User {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String fullName;

	    @Column(nullable = false)
	    private String email;

	    @Column(unique = true)
	    private String phone;

	    @Column(nullable = false)
	    private String password;

	    @Column(nullable = false)
	    private boolean active = true;

	    @Column(nullable = false)
	    private LocalDateTime createdAt = LocalDateTime.now();

	    @ManyToOne
	    @JoinColumn(name = "salon_id")
	    private Salon salon;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    private List<UserRole> userRoles = new ArrayList<>();

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }
    
    public List<UserRole> getUserRoles() {
        return userRoles;
    }

	public void setCreatedAt(LocalDateTime now) {
		this.createdAt = LocalDateTime.now();
		
	}


    
}
