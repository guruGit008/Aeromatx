package com.aeromatx.back.dto.auth; // Adjust package as necessary

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be at least 6 characters long and not exceed 40 characters")
    private String password;

    // @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters") // Added max size for company
    private String company;

    // Ensure this matches the 'role' key sent from JavaScript
    @NotBlank(message = "Account type (role) is required")
    @Size(max = 20, message = "Role cannot exceed 20 characters") // Added max size for role
    private String role; // This will typically be "CUSTOMER", "ADMIN", "MODERATOR", etc.

    // Getters and Setters (Important for Spring to bind the data)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}