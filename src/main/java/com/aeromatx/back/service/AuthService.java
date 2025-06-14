package com.aeromatx.back.service;

import com.aeromatx.back.dto.auth.JwtResponse;
import com.aeromatx.back.dto.auth.LoginRequest;
import com.aeromatx.back.dto.auth.RegisterRequest;
import com.aeromatx.back.entity.ERole;
import com.aeromatx.back.entity.Role;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.entity.RegistrationOtp; // Import the new OTP entity
import com.aeromatx.back.exception.EmailAlreadyInUseException;
import com.aeromatx.back.exception.RoleNotFoundException;
import com.aeromatx.back.exception.UsernameAlreadyExistsException;
import com.aeromatx.back.exception.OtpMismatchException; // Custom exception for OTP mismatch
import com.aeromatx.back.exception.OtpExpiredException; // Custom exception for expired OTP
import com.aeromatx.back.repository.RoleRepository;
import com.aeromatx.back.repository.UserRepository;
import com.aeromatx.back.repository.RegistrationOtpRepository; // Import the new OTP repository
import com.aeromatx.back.security.UserDetailsImpl;
import com.aeromatx.back.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional; // Import Optional
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom; // For generating OTP
import java.util.stream.Collectors;

@Service
@Transactional 
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final RegistrationOtpRepository registrationOtpRepository; // Inject OTP repository

    // Duration for which the reset token is valid (e.g., 15 minutes)
    private final long TOKEN_VALIDITY_MINUTES = 15;
    // OTP validity in minutes
    private final long OTP_VALIDITY_MINUTES = 5;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       EmailService emailService,
                       RegistrationOtpRepository registrationOtpRepository) { // Add OTP repository to constructor
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.registrationOtpRepository = registrationOtpRepository; // Initialize OTP repository
    }

    /**
     * Initiates user registration by generating and sending an OTP.
     * User data is temporarily stored until OTP is verified.
     * This method does NOT register the user to the main 'users' table.
     * @param registerRequest Contains user registration details.
     * @throws UsernameAlreadyExistsException if the username is already taken.
     * @throws EmailAlreadyInUseException if the email is already in use (for an active user).
     */
    public void initiateRegistrationWithOtp(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken!");
        }

        // Check if email is already in use by an active user
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyInUseException("Email is already in use by an existing account!");
        }
        
        // Generate a 6-digit OTP
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);

        // Store OTP and registration data temporarily
        // If an OTP already exists for this email, update it
        Optional<RegistrationOtp> existingOtp = registrationOtpRepository.findByEmail(registerRequest.getEmail());
        RegistrationOtp registrationOtp;
        if (existingOtp.isPresent()) {
            registrationOtp = existingOtp.get();
            registrationOtp.setOtp(otp);
            registrationOtp.setExpiryDate(expiryTime);
            // Update other fields as needed if the user changed them in a re-request
            registrationOtp.setUsername(registerRequest.getUsername());
            registrationOtp.setPassword(encoder.encode(registerRequest.getPassword()));
            registrationOtp.setCompany(registerRequest.getCompany());
            registrationOtp.setRole(registerRequest.getRole());
        } else {
            registrationOtp = new RegistrationOtp();
            registrationOtp.setEmail(registerRequest.getEmail());
            registrationOtp.setOtp(otp);
            registrationOtp.setExpiryDate(expiryTime);
            registrationOtp.setUsername(registerRequest.getUsername());
            registrationOtp.setPassword(encoder.encode(registerRequest.getPassword())); // Encode for temporary storage
            registrationOtp.setCompany(registerRequest.getCompany());
            registrationOtp.setRole(registerRequest.getRole());
        }
        registrationOtpRepository.save(registrationOtp);

        // Send OTP email
        emailService.sendOtpEmail(registerRequest.getEmail(), otp);
        System.out.println("OTP " + otp + " sent to " + registerRequest.getEmail() + " for registration.");
    }

    /**
     * Completes user registration by verifying the provided OTP.
     * If OTP is valid, the user is saved to the main User table.
     * @param email The user's email.
     * @param otp The OTP provided by the user.
     * @return The username of the registered user, for use in the success email.
     * @throws OtpExpiredException if the OTP has expired.
     * @throws OtpMismatchException if the OTP does not match or is invalid.
     * @throws RuntimeException if user data is not found after OTP verification.
     */
    public String completeRegistration(String email, String otp) {
        RegistrationOtp storedOtpData = registrationOtpRepository.findByEmail(email)
            .orElseThrow(() -> new OtpMismatchException("Invalid or no pending registration for this email."));

        if (storedOtpData.getExpiryDate().isBefore(LocalDateTime.now())) {
            registrationOtpRepository.delete(storedOtpData); // Clean up expired OTP
            throw new OtpExpiredException("OTP has expired. Please request a new one.");
        }

        if (!storedOtpData.getOtp().equals(otp)) {
            throw new OtpMismatchException("Invalid OTP. Please try again.");
        }

        // OTP is valid, proceed with user registration
        User user = new User(
            storedOtpData.getUsername(),
            storedOtpData.getEmail(),
            storedOtpData.getPassword(), // Password is already encoded from initiateRegistrationWithOtp
            storedOtpData.getCompany()
        );

        String strRole = storedOtpData.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRole == null || strRole.isEmpty()) {
            Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RoleNotFoundException("Error: Default role (ROLE_CUSTOMER) not found."));
            roles.add(customerRole);
        } else {
            switch (strRole.toLowerCase()) {
                case "customer":
                    Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                            .orElseThrow(() -> new RoleNotFoundException("Error: Role 'CUSTOMER' not found."));
                    roles.add(customerRole);
                    break;
                case "oem":
                    Role oemRole = roleRepository.findByName(ERole.ROLE_OEM)
                            .orElseThrow(() -> new RoleNotFoundException("Error: Role 'OEM' not found."));
                    roles.add(oemRole);
                    break;
                default:
                    throw new IllegalArgumentException("Error: Invalid role specified: '" + strRole + "'.");
            }
        }

        user.setRoles(roles);
        userRepository.save(user); // Save the new user to the main table

        registrationOtpRepository.delete(storedOtpData); // Delete the OTP record after successful registration
        System.out.println("User " + user.getUsername() + " registered successfully after OTP verification.");
        return user.getUsername(); // Return the username for the success email
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     * @param loginRequest Contains user's email and password.
     * @return JwtResponse containing JWT token and user details.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                                 userDetails.getId(),
                                 userDetails.getUsername(),
                                 userDetails.getEmail(),
                                 roles);
    }

    /**
     * Initiates the password reset process by generating a token and sending a reset email.
     * @param email The email address of the user requesting a password reset.
     * @return boolean True if the process completed (email sent or generic message), false on internal error.
     */
    public boolean initiatePasswordReset(String email) {
        return userRepository.findByEmail(email).map(user -> {
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES);

            user.setResetPasswordToken(resetToken);
            user.setResetPasswordExpiryDate(expiryDate);
            userRepository.save(user); 

            emailService.sendPasswordResetEmail(email, resetToken);
            System.out.println("Password reset email sent to : " + email);
            return true;
        }).orElseGet(() -> {
            System.out.println("Password reset requested for non-existent email : " + email + " . Sent generic success . " );
            return true;
        });
    }

    /**
     * Resets the user's password using a valid token.
     * @param token The password reset token received from the user.
     * @param newPassword The new plain-text password.
     * @return boolean True if password reset was successful, false otherwise (e.g., invalid/expired token).
     */
    public boolean resetPassword(String token, String newPassword) {
        return userRepository.findByResetPasswordToken(token).map(user -> {
            if (user.getResetPasswordExpiryDate() == null || user.getResetPasswordExpiryDate().isBefore(LocalDateTime.now())) {
                System.out.println("Attempt to use expired or invalid token : " + token + " for user : " + user.getEmail());
                user.setResetPasswordToken(null);
                user.setResetPasswordExpiryDate(null);
                userRepository.save(user);
                return false; 
            }

            user.setPassword(encoder.encode(newPassword));
            
            user.setResetPasswordToken(null);
            user.setResetPasswordExpiryDate(null);
            userRepository.save(user);
            System.out.println("Password successfully reset for user : " + user.getEmail());
            return true;
        }).orElseGet(() -> {
            System.out.println("Attempt to use a non-existent password reset token : " + token);
            return false;
        });
    }
}
