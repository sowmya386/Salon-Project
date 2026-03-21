package com.salon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Staff member with commission - premium feature for Pro/Enterprise.
 */
@Entity
@Table(name = "staff", indexes = {
    @Index(name = "idx_staff_salon", columnList = "salon_name")
})
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salon_name", nullable = false)
    private String salonName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // Optional link to User account

    private String fullName;
    private String email;
    private String phone;
    private Double commissionPercent = 0.0;  // % of service revenue
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSalonName() { return salonName; }
    public void setSalonName(String salonName) { this.salonName = salonName; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getCommissionPercent() { return commissionPercent; }
    public void setCommissionPercent(Double commissionPercent) { this.commissionPercent = commissionPercent; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
