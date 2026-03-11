package com.salon.dto;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CustomerRegisterRequest {

	 @NotBlank(message = "Full name is required")
	    private String fullName;

	    @Email(message = "Invalid email format")
	    @NotBlank(message = "Email is required")
	    private String email;

	    @Size(min = 6, message = "Password must be at least 6 characters")
	    @NotBlank(message = "Password is required")
	    private String password;

	    @NotBlank(message = "Phone is required")
	    private String phone;

	    @NotNull(message = "Salonname is required")
	    private String salonName;
	    
	    public String getFullName() { return fullName; }
	    public void setFullName(String fullName) { this.fullName = fullName; }

	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }

	    public String getPassword() { return password; }
	    public void setPassword(String password) { this.password = password; }

	    public String getPhone() { return phone; }
	    public void setPhone(String phone) { this.phone = phone; }

	    public String getSalonName() { return salonName; }
	    public void setSalonName(String salonName) { this.salonName = salonName; }
}
