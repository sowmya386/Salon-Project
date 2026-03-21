package com.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

	 @Email(message = "Invalid email format")
	    @NotBlank(message = "Email is required")
	    private String email;

	    @NotBlank(message = "Password is required")
	    private String password;

	    /** Salon name - optional for single-tenant (uses default from config) */
	    private String salonName;

	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }

	    public String getPassword() { return password; }
	    public void setPassword(String password) { this.password = password; }

	    public String getSalonName() { return salonName; }
	    public void setSalonName(String salonName) { this.salonName = salonName; }
	}
