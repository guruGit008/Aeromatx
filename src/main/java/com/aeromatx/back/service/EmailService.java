package com.aeromatx.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // You might want to get the sender email from application.properties
    // @Value("${spring.mail.username}") 
    // private String senderEmail;

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = "http://localhost:8080/reset-password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("darshantiranga@gmail.com"); // CHANGE THIS TO YOUR ACTUAL SENDER EMAIL
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n" + resetLink);

        mailSender.send(message);
    }

    /**
     * Sends a congratulatory email upon successful user registration.
     * @param to The email address of the newly registered user.
     * @param username The username of the newly registered user.
     */
    public void sendRegistrationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("darshantiranga@gmail.com"); // CHANGE THIS TO YOUR ACTUAL SENDER EMAIL
        message.setTo(to);
        message.setSubject("Registration Successful!");
        message.setText("Dear " + username + ",\n\nCongratulations! Your registration was successful.\n\nWelcome to our application!\n\nSincerely,\nYour Application Team");

        mailSender.send(message);
    }

    /**
     * Sends an OTP (One-Time Password) to the user's email for registration verification.
     * @param to The email address to send the OTP to.
     * @param otp The generated OTP.
     */
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("darshantiranga@gmail.com"); // CHANGE THIS TO YOUR ACTUAL SENDER EMAIL
        message.setTo(to);
        message.setSubject("Your Registration OTP");
        message.setText("Dear User,\n\nYour One-Time Password (OTP) for registration is: " + otp + "\n\nThis OTP is valid for 5 minutes. Please do not share it with anyone.\n\nSincerely,\nYour Application Team");
        
        mailSender.send(message);
    }

        public void sendTestEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("darshantiranga@gmail.com"); // CHANGE THIS TO YOUR ACTUAL SENDER EMAIL FROM application.properties
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}