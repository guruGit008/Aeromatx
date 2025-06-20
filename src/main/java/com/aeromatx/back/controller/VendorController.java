// src/main/java/com/aeromatx/back/controller/VendorController.java
package com.aeromatx.back.controller;

import com.aeromatx.back.dto.user.VendorRegistrationRequest; // ✅ Correct

import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.service.VendorService;
import jakarta.validation.Valid; // Import for @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// REST controller for vendor-related operations
@RestController
@RequestMapping("/api/vendor") // Base path for vendor API
public class VendorController {

    @Autowired // Injecting the VendorService dependency
    private VendorService vendorService;

    // Endpoint to handle vendor registration form submission (CREATE operation)
    @PostMapping("/register")
    // @Valid tells Spring to apply validation rules defined in VendorRegistrationRequest DTO
    public ResponseEntity<Map<String, String>> registerVendor(@Valid @RequestBody VendorRegistrationRequest request) {
        try {
            vendorService.registerNewVendor(request);
            return ResponseEntity.ok(Map.of("message", "Vendor application submitted successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error during vendor registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "An unexpected error occurred during registration."));
        }
    }

    // Endpoint to get a single vendor by ID (READ operation - single)
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Optional<Vendor> vendor = vendorService.getVendorById(id);
        return vendor.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to get all vendors (READ operation - all)
@GetMapping
public ResponseEntity<List<Vendor>> getAllVendors() {
    List<Vendor> vendors = vendorService.getAllVendors();
    if (vendors.isEmpty()) {
        return ResponseEntity.noContent().build(); // This sends a 204 No Content
    }
    return ResponseEntity.ok(vendors); // This sends a 200 OK with JSON array
}
    // New endpoint to update an existing vendor (UPDATE operation)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateVendor(@PathVariable Long id, @Valid @RequestBody VendorRegistrationRequest request) {
        try {
            vendorService.updateVendor(id, request);
            return ResponseEntity.ok(Map.of("message", "Vendor updated successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error updating vendor with ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "An unexpected error occurred during vendor update."));
        }
    }

    // New endpoint to delete a vendor by ID (DELETE operation)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVendor(@PathVariable Long id) {
        try {
            vendorService.deleteVendor(id);
            return ResponseEntity.ok(Map.of("message", "Vendor deleted successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error deleting vendor with ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "An unexpected error occurred during vendor deletion."));
        }
    }
}
