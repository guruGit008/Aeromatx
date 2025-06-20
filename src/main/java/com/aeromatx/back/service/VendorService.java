// src/main/java/com/aeromatx/back/service/VendorService.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.user.VendorRegistrationRequest; // ✅ Correct

import com.aeromatx.back.entity.Vendor;
import java.util.List;
import java.util.Optional;

// Interface for vendor-related business logic
public interface VendorService {
    // Method to register a new vendor (CREATE)
    void registerNewVendor(VendorRegistrationRequest request);

    // Method to get a vendor by ID (READ - single)
    Optional<Vendor> getVendorById(Long id);

    // Method to get all vendors (READ - all)
    List<Vendor> getAllVendors();

    // Method to update an existing vendor (UPDATE)
    Vendor updateVendor(Long id, VendorRegistrationRequest request);

    // Method to delete a vendor by ID (DELETE)
    void deleteVendor(Long id);
}