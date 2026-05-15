package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1 - Generate OTP and send to email
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Generate 6 digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Set OTP expiry to 10 minutes from now
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send OTP email
        String subject = "Password Reset OTP - Job Portal";
        String body = "Hi " + user.getName() + ",\n\n"
                + "Your OTP for password reset is: " + otp + "\n\n"
                + "This OTP is valid for 10 minutes only.\n"
                + "Do not share this OTP with anyone.\n\n"
                + "If you did not request this, ignore this email.\n\n"
                + "Best regards,\nJob Portal Team";

        emailService.sendEmail(email, subject, body);
    }

    // Step 2 - Verify OTP
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Check if OTP matches
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Check if OTP is expired
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired, please request a new one");
        }
    }

    // Step 3 - Reset Password
    public void resetPassword(String email, String otp, String newPassword) {
        // Verify OTP first
        verifyOtp(email, otp);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear OTP after successful reset
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }
}