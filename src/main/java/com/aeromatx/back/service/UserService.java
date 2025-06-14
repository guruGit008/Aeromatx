package com.aeromatx.back.service;

import com.aeromatx.back.dto.user.UserDto;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Import Optional
import java.util.stream.Collectors;

@Service
public class UserService { 

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Method required by UserController: getAllUsers()
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch all users from repository
    }

    // Method required by UserController: getUserById(Long id)
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); // Fetch user by ID from repository
    }

    // Method for the new endpoint: findUsersByRole(String roleName)
    @Transactional(readOnly = true) 
    public List<UserDto> findUsersByRole(String roleName) {
        String effectiveRoleName = roleName.toUpperCase(); 
        // Uncomment if roles are stored with "ROLE_" prefix
        // if (!effectiveRoleName.startsWith("ROLE_")) { effectiveRoleName = "ROLE_" + effectiveRoleName; }
        
        List<User> users = userRepository.findByRoleName(effectiveRoleName);
        
        return users.stream()
                    .map(UserDto::fromEntity) // Convert each User entity to UserDto
                    .collect(Collectors.toList());
    }
    
    // Add other user-related service methods here if needed...
}

