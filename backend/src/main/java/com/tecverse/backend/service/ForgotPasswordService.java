package com.tecverse.backend.service;

import com.tecverse.backend.dto.ApiResponse;
import com.tecverse.backend.dto.ForgotPasswordRequest;
import com.tecverse.backend.dto.ResetPasswordRequest;
import com.tecverse.backend.dto.VerifyOtpRequest;
import com.tecverse.backend.entity.TicketRegistration;
import com.tecverse.backend.repository.TicketRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ForgotPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordService.class);
    private static final long OTP_VALIDITY_SECONDS = 10 * 60; // 10 minutes

    private final TicketRegistrationRepository ticketRegistrationRepository;
    private final MailDeliveryService mailDeliveryService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    // In-memory thread-safe OTP store: key = normalized email -> OtpRecord
    private final ConcurrentHashMap<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

    public ForgotPasswordService(
            TicketRegistrationRepository ticketRegistrationRepository,
            MailDeliveryService mailDeliveryService,
            PasswordEncoder passwordEncoder) {
        this.ticketRegistrationRepository = ticketRegistrationRepository;
        this.mailDeliveryService = mailDeliveryService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Step 1: Send OTP to registered email address
     */
    public ApiResponse sendOtp(ForgotPasswordRequest request) {
        if (request == null) {
            return ApiResponse.error("Invalid request");
        }

        String input = request.getEffectiveEmailOrIdentifier();
        if (input.isEmpty()) {
            return ApiResponse.error("Please enter your registered email address.");
        }

        input = input.trim();

        // Find user by email or identifier in tecverse_ticket_registrations table
        Optional<TicketRegistration> userOpt;
        if (input.contains("@")) {
            userOpt = ticketRegistrationRepository.findByEmailIgnoreCase(input);
        } else {
            userOpt = ticketRegistrationRepository.findByIdentifier(input);
        }

        if (userOpt.isEmpty()) {
            log.warn("Forgot Password: No registered account found for identifier '{}'", input);
            return ApiResponse.error("No registered account found with this email address. Please register on the TEC-VERSE website.");
        }

        TicketRegistration user = userOpt.get();
        String recipientEmail = user.getEmail();
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return ApiResponse.error("No valid email address associated with this registered account.");
        }

        recipientEmail = recipientEmail.trim().toLowerCase();

        // Generate 6-digit numeric OTP
        int otpNumber = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpNumber);

        // Store OTP with expiry
        Instant expiresAt = Instant.now().plusSeconds(OTP_VALIDITY_SECONDS);
        otpStore.put(recipientEmail, new OtpRecord(otp, expiresAt, false));

        // Compose email content
        String attendeeName = user.getOfficialName() != null && !user.getOfficialName().isBlank()
                ? user.getOfficialName().trim()
                : "Attendee";

        String subject = "TEC-VERSE 2026 - Password Reset Verification Code";
        String body = "Dear " + attendeeName + ",\n\n"
                + "We received a request to reset the password for your TEC-VERSE 2026 account.\n\n"
                + "Your 6-Digit One-Time Password (OTP) verification code is:\n\n"
                + "========================================\n"
                + "               " + otp + "\n"
                + "========================================\n\n"
                + "This OTP is valid for 10 minutes.\n"
                + "Please enter this code on the application to verify your identity and set a new password.\n\n"
                + "If you did not request a password reset, please ignore this email. Your current password remains secure.\n\n"
                + "Regards,\n"
                + "TEC-VERSE 2026 Organizing Committee\n"
                + "Centre for Development of Advanced Computing (C-DAC)\n"
                + "Ministry of Electronics and Information Technology (MeitY), Government of India\n";

        try {
            boolean sent = mailDeliveryService.sendSimple(recipientEmail, subject, body, "Forgot Password OTP");
            log.info("OTP generation successful for {}. Delivery result: {}", recipientEmail, sent);

            Map<String, Object> data = new HashMap<>();
            data.put("email", recipientEmail);
            data.put("maskedEmail", maskEmail(recipientEmail));

            return ApiResponse.success("OTP verification code has been sent to " + maskEmail(recipientEmail) + ".", data);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", recipientEmail, e.getMessage());
            return ApiResponse.error("Failed to send email. Please check your connection and try again.");
        }
    }

    /**
     * Step 2: Verify OTP
     */
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        if (request == null) {
            return ApiResponse.error("Invalid request");
        }

        String input = request.getEffectiveEmailOrIdentifier();
        String otp = request.getOtp();

        if (input.isEmpty() || otp == null || otp.trim().isEmpty()) {
            return ApiResponse.error("Please enter the registered email and 6-digit OTP.");
        }

        String normalizedEmail = resolveUserEmail(input);
        if (normalizedEmail == null) {
            return ApiResponse.error("No registered account found with this email address.");
        }

        OtpRecord record = otpStore.get(normalizedEmail);
        if (record == null) {
            return ApiResponse.error("No active OTP found or OTP has expired. Please request a new OTP.");
        }

        if (Instant.now().isAfter(record.expiresAt)) {
            otpStore.remove(normalizedEmail);
            return ApiResponse.error("OTP has expired. Please request a new OTP.");
        }

        if (!record.otp.equals(otp.trim())) {
            return ApiResponse.error("Invalid OTP code. Please check your email and enter the correct 6-digit code.");
        }

        // Mark OTP as verified for password change
        record.verified = true;
        otpStore.put(normalizedEmail, record);

        log.info("OTP verified successfully for {}", normalizedEmail);
        return ApiResponse.success("OTP verified successfully. You can now set your new password.");
    }

    /**
     * Step 3: Reset password & save hashed in database
     */
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        if (request == null) {
            return ApiResponse.error("Invalid request");
        }

        String input = request.getEffectiveEmailOrIdentifier();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (input.isEmpty() || otp == null || otp.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ApiResponse.error("Please provide email, OTP, and new password.");
        }

        newPassword = newPassword.trim();
        if (newPassword.length() < 6) {
            return ApiResponse.error("Password must be at least 6 characters long.");
        }

        if (confirmPassword != null && !confirmPassword.trim().isEmpty() && !newPassword.equals(confirmPassword.trim())) {
            return ApiResponse.error("New password and confirm password do not match.");
        }

        String normalizedEmail = resolveUserEmail(input);
        if (normalizedEmail == null) {
            return ApiResponse.error("No registered account found with this email address.");
        }

        OtpRecord record = otpStore.get(normalizedEmail);
        if (record == null) {
            return ApiResponse.error("Session expired. Please request a new OTP verification code.");
        }

        if (Instant.now().isAfter(record.expiresAt)) {
            otpStore.remove(normalizedEmail);
            return ApiResponse.error("OTP has expired. Please request a new OTP.");
        }

        if (!record.otp.equals(otp.trim())) {
            return ApiResponse.error("Invalid OTP code. Password reset aborted.");
        }

        // Find user in database
        Optional<TicketRegistration> userOpt = ticketRegistrationRepository.findByEmailIgnoreCase(normalizedEmail);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("User record not found in database.");
        }

        TicketRegistration user = userOpt.get();

        // Hash new password using BCrypt
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());

        ticketRegistrationRepository.save(user);

        // Invalidate OTP
        otpStore.remove(normalizedEmail);

        log.info("Password successfully updated and hashed in database for user: {}", normalizedEmail);
        return ApiResponse.success("Password has been changed successfully. You can now login with your new password.");
    }

    private String resolveUserEmail(String input) {
        if (input.contains("@")) {
            return input.trim().toLowerCase();
        }
        Optional<TicketRegistration> userOpt = ticketRegistrationRepository.findByIdentifier(input.trim());
        return userOpt.map(ticketRegistration -> ticketRegistration.getEmail().trim().toLowerCase()).orElse(null);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name + "***@" + domain;
        }
        return name.substring(0, 2) + "***" + name.charAt(name.length() - 1) + "@" + domain;
    }

    private static class OtpRecord {
        final String otp;
        final Instant expiresAt;
        boolean verified;

        OtpRecord(String otp, Instant expiresAt, boolean verified) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.verified = verified;
        }
    }
}
