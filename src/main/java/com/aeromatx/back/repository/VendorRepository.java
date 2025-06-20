// src/main/java/com/aeromatx/back/repository/VendorRepository.java
package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JPA Repository interface for Vendor entities
@Repository // Marks this interface as a Spring Data Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    // Custom query method to find a Vendor by email address
    Optional<Vendor> findByEmail(String email);

    // You can add more custom query methods here as needed, e.g.:
    // List<Vendor> findByStatus(String status);
    // Optional<Vendor> findByBusinessName(String businessName);
}