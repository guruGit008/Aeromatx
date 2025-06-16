package com.aeromatx.back.controller;

import com.aeromatx.back.dto.user.UserDto;
import com.aeromatx.back.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Ensure this import is present
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080") // Crucial for frontend access
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
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get own profile or admin can get any user's profile - Returns UserDto
    @GetMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\') or #id == authentication.principal.id")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint for Retrieving Users by Role - Returns List<UserDto>
    @GetMapping("/by-role")
    // @PreAuthorize("hasRole(\'ADMIN\')") // COMMENTED OUT: This endpoint now allows all access
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role) {
        List<UserDto> users = userService.findUsersByRole(role);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}