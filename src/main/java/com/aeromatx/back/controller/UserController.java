package com.aeromatx.back.controller;

// Removed import for UserProfileDto
import com.aeromatx.back.dto.user.UserDto; // Using UserDto everywhere now
// import com.aeromatx.back.entity.User;
import com.aeromatx.back.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // === Endpoints now consistently using UserDto ===

    // Admin only - Returns List<UserDto>
    @GetMapping
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // Use UserDto.fromEntity for conversion
        List<UserDto> users = userService.getAllUsers().stream()
                .map(UserDto::fromEntity) // Convert User entity to UserDto
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Get own profile or admin can get any user's profile - Returns UserDto
    @GetMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\') or #id == authentication.principal.id") 
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        // Use UserDto.fromEntity for conversion
        return userService.getUserById(id)
                .map(UserDto::fromEntity) // Convert User entity to UserDto
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // New Endpoint for Retrieving Users by Role - Returns List<UserDto>
    @GetMapping("/by-role")
    // @PreAuthorize("hasRole(\'ADMIN\')") // Optional security
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role) {
        List<UserDto> users = userService.findUsersByRole(role);
        if (users.isEmpty()) {


            
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    // Removed the private convertToDto helper method as it's no longer needed

    // ... other user-related endpoints ...

}

