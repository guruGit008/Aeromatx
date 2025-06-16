// src/main/java/com/aeromatx/back/repository/UserRepository.java

package com.aeromatx.back.repository;

import com.aeromatx.back.entity.ERole; // <--- ADD THIS IMPORT
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

    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    // CHANGE THIS LINE: Accept ERole as parameter
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") ERole roleName); // <--- Changed from String to ERole
}