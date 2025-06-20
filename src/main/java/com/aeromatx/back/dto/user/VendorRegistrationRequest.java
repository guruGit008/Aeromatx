// src/main/java/com/aeromatx/back/dto/VendorRegistrationRequest.java
package com.aeromatx.back.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO to capture all fields from the vendor_register.html form
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Lombok annotation to generate an all-args constructor
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
public class VendorRegistrationRequest {
    // Company Contact Information
    @NotBlank(message = "Business Name is required")
    private String businessName;
    @NotBlank(message = "Company Address is required")
    private String companyAddress;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State / Province is required")
    private String state;
    @NotBlank(message = "Postal / Zip Code is required")
    // Updated pattern for more global postal code compatibility.
    // This pattern allows for digits, letters, spaces, and hyphens, typically found in international postal codes.
    // Length is between 3 and 10 characters, a common range for many countries.
    @Pattern(regexp = "^[a-zA-Z0-9\\s-]{3,10}$", message = "Invalid Postal/Zip Code format. Must be 3-10 characters (letters, numbers, spaces, hyphens).")
    private String zipCode;
    // Website is optional, so no @NotBlank
    private String website;

    // Point of Contact
    @NotBlank(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "Last Name is required")
    private String lastName;
    @NotBlank(message = "Day Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format (e.g., +12345678900 or 1234567890)") // Basic international phone format
    private String phoneDay;
    // Phone Evening is optional
    private String phoneEvening;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Position in Company is required")
    private String position;

    // Company Overview
    @NotBlank(message = "General Details of Services/Goods are required")
    @Size(min = 10, message = "Service details must be at least 10 characters long")
    private String serviceDetails;
    @NotBlank(message = "Establishment Date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Establishment Date must be inYYYY-MM-DD format") // Enforce date format for consistency
    private String establishmentDate;
    @NotBlank(message = "Geographic Service Area is required")
    private String serviceArea;
    @NotBlank(message = "Business Type is required")
    private String businessType;
    // Employee Count is optional
    private Integer employeeCount;
    @NotBlank(message = "Insured status is required")
    private String insured; // "Yes" or "No"
    @NotBlank(message = "Licensed status is required")
    private String licensed; // "Yes" or "No"
    // License Number is optional
    private String licenseNumber;
    // Annual Sales is optional
    private String annualSales;

    // Banking Information
    @NotBlank(message = "Bank Name is required")
    private String bankName;
    @NotBlank(message = "Beneficiary Name is required")
    private String beneficiaryName;
    @NotBlank(message = "Account Number is required")
    private String accountNumber;
    // Routing Number is optional
    private String routingNumber;

    // Certification
    @NotBlank(message = "Submission Date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Submission Date must be inYYYY-MM-DD format") // Enforce date format
    private String submissionDate;
    @NotBlank(message = "Company Representative Signature is required")
    private String signature;

    // Admin/Internal fields that might be updated via this DTO during an 'edit' operation
    // Even if hidden on frontend, it needs to be present here for backend processing.
    // If you always want it to be '0.0' or derived, you can remove from DTO and set in service.
    private String status;
    private String ratings; // Added back the ratings field
}
