package com.aeromatx.back.controller;

import com.aeromatx.back.dto.auth.ForgotPasswordRequest;
import com.aeromatx.back.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        emailService.sendTestEmail(request.getEmail(), "Reset Password", "Click here to reset your password.");
        return ResponseEntity.ok("Password reset email sent!");
    }
}

