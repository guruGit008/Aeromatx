package com.aeromatx.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Import for LocalDateTime
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
        })
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Add the 'company' field
    @Column(name = "company") // Maps to the 'company' column in the database
    private String company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // --- New fields for Password Reset functionality ---
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_expiry_date")
    private LocalDateTime resetPasswordExpiryDate;
    // --- End New fields ---

    // Custom constructor for registration, now including 'company'
    // Note: Lombok's @AllArgsConstructor will create a constructor with (id, username, email, password, company, roles, resetPasswordToken, resetPasswordExpiryDate)
    // If you specifically need this exact constructor without 'id' and 'roles' for registration, keep it.
    // Otherwise, you might rely on setters or Lombok's @AllArgsConstructor.
    public User(String username, String email, String password, String company) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.company = company; // Initialize the company field
        this.roles = new HashSet<>(); // Initialize roles to prevent NullPointerException
        // New fields for password reset are not initialized here, they will be set by the service
    }
}
