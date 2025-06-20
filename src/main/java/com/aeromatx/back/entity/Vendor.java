// src/main/java/com/aeromatx/back/entity/Vendor.java
package com.aeromatx.back.entity;

import jakarta.persistence.*; // Using Jakarta Persistence API (JPA) for Spring Boot 3+
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // For date fields

// JPA Entity representing the 'vendors' table in the database
@Entity // Marks this class as a JPA entity
@Table(name = "vendors") // Specifies the table name in the database
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Lombok annotation to generate an all-args constructor
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
public class Vendor {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments ID
    private Long id; // Primary key for the vendor

    // Company Contact Information
    @Column(nullable = false) // Not null column
    private String businessName;
    @Column(nullable = false)
    private String companyAddress;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String zipCode;
    private String website;

    // Point of Contact
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phoneDay;
    private String phoneEvening;
    @Column(nullable = false, unique = true) // Email should be unique for each vendor
    private String email;
    @Column(nullable = false)
    private String position;

    // Company Overview
    @Lob // For larger text fields like descriptions
    @Column(nullable = false)
    private String serviceDetails;
    @Column(nullable = false)
    private LocalDate establishmentDate; // Store as LocalDate for proper date handling
    @Column(nullable = false)
    private String serviceArea;
    @Column(nullable = false)
    private String businessType;
    private Integer employeeCount; // Can be null
    @Column(nullable = false)
    private String insured; // "Yes" or "No"
    @Column(nullable = false)
    private String licensed; // "Yes" or "No"
    private String licenseNumber; // Can be null
    private String annualSales;   // Can be null

    // Banking Information
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String beneficiaryName;
    @Column(nullable = false)
    private String accountNumber;
    private String routingNumber; // Can be null

    // Certification
    @Column(nullable = false)
    private LocalDate submissionDate; // Store as LocalDate
    @Column(nullable = false)
    private String signature; // Storing typed name as signature

    // Admin/Internal fields
    @Column(nullable = false)
    private String status; // e.g., "Pending", "Approved", "Rejected"
    @Column(nullable = false)
    private String ratings; // Initial rating "0.0"
}
