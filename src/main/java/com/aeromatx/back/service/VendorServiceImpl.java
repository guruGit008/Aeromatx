// src/main/java/com/aeromatx/back/service/VendorServiceImpl.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.user.VendorRegistrationRequest; // ✅ Correct

import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

// Implementation of the VendorService interface
@Service // Marks this class as a Spring Service component
public class VendorServiceImpl implements VendorService {

    @Autowired // Injecting the VendorRepository dependency
    private VendorRepository vendorRepository;

    @Override
    @Transactional // Ensures all database operations are atomic
    public void registerNewVendor(VendorRegistrationRequest request) {
        // Business logic validation: check for duplicate email
        Optional<Vendor> existingVendor = vendorRepository.findByEmail(request.getEmail());
        if (existingVendor.isPresent()) {
            throw new IllegalArgumentException("A vendor with this email already exists.");
        }

        // Create a new Vendor entity from the DTO
        Vendor vendor = new Vendor();
        
        // Map fields from request DTO to Vendor entity
        vendor.setBusinessName(request.getBusinessName());
        vendor.setCompanyAddress(request.getCompanyAddress());
        vendor.setCity(request.getCity());
        vendor.setState(request.getState());
        vendor.setZipCode(request.getZipCode());
        vendor.setWebsite(request.getWebsite());
        vendor.setFirstName(request.getFirstName());
        vendor.setLastName(request.getLastName());
        vendor.setPhoneDay(request.getPhoneDay());
        vendor.setPhoneEvening(request.getPhoneEvening());
        vendor.setEmail(request.getEmail());
        vendor.setPosition(request.getPosition());
        vendor.setServiceDetails(request.getServiceDetails());

        // Parse establishment date string to LocalDate
        try {
            vendor.setEstablishmentDate(LocalDate.parse(request.getEstablishmentDate()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid establishment date format. ExpectedYYYY-MM-DD.");
        }

        vendor.setServiceArea(request.getServiceArea());
        vendor.setBusinessType(request.getBusinessType());
        vendor.setEmployeeCount(request.getEmployeeCount());
        vendor.setInsured(request.getInsured());
        vendor.setLicensed(request.getLicensed());
        vendor.setLicenseNumber(request.getLicenseNumber());
        vendor.setAnnualSales(request.getAnnualSales());
        vendor.setBankName(request.getBankName());
        vendor.setBeneficiaryName(request.getBeneficiaryName());
        vendor.setAccountNumber(request.getAccountNumber());
        vendor.setRoutingNumber(request.getRoutingNumber());

        // Parse submission date string to LocalDate
        try {
            vendor.setSubmissionDate(LocalDate.parse(request.getSubmissionDate()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid submission date format. ExpectedYYYY-MM-DD.");
        }
        
        vendor.setSignature(request.getSignature());

        // Set default values for status and ratings as they are internal to the application
        vendor.setStatus("Pending"); // All new registrations are pending review
        vendor.setRatings("0.0"); // Initial rating

        // Save the vendor entity to the database
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public Optional<Vendor> getVendorById(Long id) {
        return vendorRepository.findById(id); // Use JpaRepository's findById method
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll(); // Use JpaRepository's findAll method
    }

    @Override // THIS METHOD WAS MISSING IN YOUR PROVIDED CODE
    @Transactional // Transactional for update operation
    public Vendor updateVendor(Long id, VendorRegistrationRequest request) {
        // Find the existing vendor by ID
        Optional<Vendor> existingVendorOptional = vendorRepository.findById(id);
        if (existingVendorOptional.isEmpty()) {
            throw new IllegalArgumentException("Vendor with ID " + id + " not found for update.");
        }
        Vendor existingVendor = existingVendorOptional.get();

        // Check for email change leading to duplicate (if email is changed to an existing one by another vendor)
        if (!existingVendor.getEmail().equalsIgnoreCase(request.getEmail())) { // Case-insensitive check
            if (vendorRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Cannot update: Another vendor already uses this email.");
            }
        }

        // Update fields from the request DTO to the existing Vendor entity
        existingVendor.setBusinessName(request.getBusinessName());
        existingVendor.setCompanyAddress(request.getCompanyAddress());
        existingVendor.setCity(request.getCity());
        existingVendor.setState(request.getState());
        existingVendor.setZipCode(request.getZipCode());
        existingVendor.setWebsite(request.getWebsite());
        existingVendor.setFirstName(request.getFirstName());
        existingVendor.setLastName(request.getLastName());
        existingVendor.setPhoneDay(request.getPhoneDay());
        existingVendor.setPhoneEvening(request.getPhoneEvening());
        existingVendor.setEmail(request.getEmail());
        existingVendor.setPosition(request.getPosition());
        existingVendor.setServiceDetails(request.getServiceDetails());

        try {
            existingVendor.setEstablishmentDate(LocalDate.parse(request.getEstablishmentDate()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid establishment date format. ExpectedYYYY-MM-DD.");
        }

        existingVendor.setServiceArea(request.getServiceArea());
        existingVendor.setBusinessType(request.getBusinessType());
        existingVendor.setEmployeeCount(request.getEmployeeCount());
        existingVendor.setInsured(request.getInsured());
        existingVendor.setLicensed(request.getLicensed());
        existingVendor.setLicenseNumber(request.getLicenseNumber());
        existingVendor.setAnnualSales(request.getAnnualSales());
        existingVendor.setBankName(request.getBankName());
        existingVendor.setBeneficiaryName(request.getBeneficiaryName());
        existingVendor.setAccountNumber(request.getAccountNumber());
        existingVendor.setRoutingNumber(request.getRoutingNumber());

        try {
            existingVendor.setSubmissionDate(LocalDate.parse(request.getSubmissionDate()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid submission date format. ExpectedYYYY-MM-DD.");
        }
        
        existingVendor.setSignature(request.getSignature());

        // Update status and ratings from the request as well (admin can change these)
        existingVendor.setStatus(request.getStatus());
        existingVendor.setRatings(request.getRatings()); 

        // Save the updated vendor entity
        return vendorRepository.save(existingVendor);
    }

    @Override // THIS METHOD WAS MISSING IN YOUR PROVIDED CODE
    @Transactional // Transactional for delete operation
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new IllegalArgumentException("Vendor with ID " + id + " not found for deletion.");
        }
        vendorRepository.deleteById(id);
    }
}
