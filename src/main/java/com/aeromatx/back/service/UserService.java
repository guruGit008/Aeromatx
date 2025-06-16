// src/main/java/com/aeromatx/back/service/UserService.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.user.UserDto;
import com.aeromatx.back.entity.ERole;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Corrected: Now returns List<UserDto>
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity) // Convert each User entity to UserDto
                .collect(Collectors.toList());
    }

    // Corrected: Now returns Optional<UserDto>
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::fromEntity); // Convert User entity to UserDto if present
    }

    @Transactional(readOnly = true)
    public List<UserDto> findUsersByRole(String roleName) {
        String effectiveRoleName = roleName.toUpperCase();

        if (!effectiveRoleName.startsWith("ROLE_")) {
            effectiveRoleName = "ROLE_" + effectiveRoleName;
        }

        ERole enumRole;
        try {
            enumRole = ERole.valueOf(effectiveRoleName);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role name requested: " + roleName + ". Error: " + e.getMessage());
            return Collections.emptyList();
        }

        List<User> users = userRepository.findByRoleName(enumRole);

        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id).map(existingUser -> {
            if (userDto.getUsername() != null && !userDto.getUsername().isBlank()) {
                existingUser.setUsername(userDto.getUsername());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
                existingUser.setEmail(userDto.getEmail());
            }
            if (userDto.getCompany() != null) {
                existingUser.setCompany(userDto.getCompany());
            }

            User updatedUser = userRepository.save(existingUser);
            return UserDto.fromEntity(updatedUser);
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}