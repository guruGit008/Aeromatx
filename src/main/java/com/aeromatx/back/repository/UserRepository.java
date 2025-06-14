package com.aeromatx.back.repository;

import com.aeromatx.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);

    // --- New method for Password Reset functionality ---
    // Finds a user by their password reset token
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    // --- End New method ---

    // Existing method to find users by role name
    // Assumes your Role entity has a 'name' field (e.g., "ROLE_CUSTOMER", "ROLE_OEM")
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
}
