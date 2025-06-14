package com.aeromatx.back.controller;

import com.aeromatx.back.dto.auth.JwtResponse;
import com.aeromatx.back.dto.auth.LoginRequest;
import com.aeromatx.back.dto.auth.RegisterRequest;
import com.aeromatx.back.dto.auth.VerificationRequest; // New DTO for OTP verification
// DTOs for forgot/reset password requests
import com.aeromatx.back.dto.auth.ForgotPasswordRequest;
import com.aeromatx.back.dto.auth.ResetPasswordRequest;

import com.aeromatx.back.service.AuthService;
import com.aeromatx.back.service.EmailService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base path for all authentication-related endpoints
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService; 

    // Using constructor injection for all dependencies - highly recommended
    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService; 
    }

    /**
     * Initiates the registration process by sending an OTP to the user's email.
     * The user is NOT registered in the database at this point.
     * @param registerRequest Contains user registration details (username, email, password, etc.).
     * @return ResponseEntity indicating if the OTP was sent successfully.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> initiateRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Initiate registration: save user data temporarily and send OTP email.
            // THIS METHOD SHOULD NOT register the user to the main 'users' table yet.
            authService.initiateRegistrationWithOtp(registerRequest); 
            
            Map<String, String> responseBody = new HashMap<>();
            // Message to indicate that an OTP has been sent.
            responseBody.put("message", "OTP sent to your email. Please verify to complete registration.");
            return new ResponseEntity<>(responseBody, HttpStatus.OK); 
            
        } catch (RuntimeException e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Completes the user registration process after OTP verification.
     * @param verificationRequest Contains the user's email and the OTP they entered.
     * @return ResponseEntity indicating registration success or failure.
     */
    @PostMapping("/register/verify-otp") // New endpoint for OTP verification
    public ResponseEntity<Map<String, String>> verifyRegistrationOtp(@Valid @RequestBody VerificationRequest verificationRequest) {
        try {
            // Verify OTP and then complete user registration.
            // This method in AuthService will save the user to the 'users' table.
            String registeredUsername = authService.completeRegistration(verificationRequest.getEmail(), verificationRequest.getOtp());

            // Send congratulatory email ONLY after successful OTP verification AND user registration
            // Use the actual username obtained from the completeRegistration method
            emailService.sendRegistrationEmail(verificationRequest.getEmail(), registeredUsername); 

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "User registered successfully!");
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest); // Delegate to AuthService
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of(
                "error", "Authentication Failed",
                "message", "Invalid email or password."
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Handles the request to initiate a password reset.
     * Receives an email and calls the AuthService to send a reset link.
     * Always returns a generic success message for security reasons.
     * Frontend will call: POST /api/auth/forgot-password
     * @param request Contains the email address for password reset.
     * @return ResponseEntity with a success message (always generic).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "If an account with that email exists, a password reset link has been sent to your inbox.");
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Handles the request to reset the password using a provided token and new password.
     * Frontend will call: POST /api/auth/reset-password
     * @param request Contains the reset token and the new password.
     * @return ResponseEntity indicating success or failure of the password reset.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "New password must be at least 8 characters long.");
            return ResponseEntity.badRequest().body(errorBody);
        }

        boolean resetSuccess = authService.resetPassword(request.getToken(), request.getNewPassword());

        if (resetSuccess) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Your password has been successfully reset.");
            return ResponseEntity.ok(responseBody);
        } else {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "Invalid or expired password reset token. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }
    }
}