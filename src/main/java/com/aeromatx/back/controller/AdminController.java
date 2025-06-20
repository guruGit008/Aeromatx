package com.aeromatx.back.controller;


import com.aeromatx.back.dto.auth.ChangePasswordRequest;
import com.aeromatx.back.security.UserDetailsImpl;
import com.aeromatx.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // 👈 Only accessible by ADMIN role
public class AdminController {

    @Autowired
    private UserService userService;

    // Get admin profile details
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(Authentication authentication) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(adminDetails.getId()));
    }

    // Change password endpoint
    @PutMapping("/profile/password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.changePassword(
                adminDetails.getId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
    }
}