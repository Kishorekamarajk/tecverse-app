package com.tecverse.backend.controller;

import com.tecverse.backend.dto.ApiResponse;
import com.tecverse.backend.dto.ForgotPasswordRequest;
import com.tecverse.backend.dto.ResetPasswordRequest;
import com.tecverse.backend.dto.VerifyOtpRequest;
import com.tecverse.backend.service.ForgotPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    /**
     * Send OTP to registered email
     * POST /api/auth/forgot-password
     * POST /api/auth/forgot-password/send-otp
     */
    @PostMapping(value = {"/forgot-password", "/forgot-password/send-otp"})
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody ForgotPasswordRequest request) {
        ApiResponse response = forgotPasswordService.sendOtp(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Verify OTP
     * POST /api/auth/verify-reset-otp
     * POST /api/auth/forgot-password/verify-otp
     */
    @PostMapping(value = {"/verify-reset-otp", "/forgot-password/verify-otp"})
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        ApiResponse response = forgotPasswordService.verifyOtp(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Reset Password and save hashed in DB
     * POST /api/auth/reset-password
     * POST /api/auth/forgot-password/reset
     */
    @PostMapping(value = {"/reset-password", "/forgot-password/reset"})
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        ApiResponse response = forgotPasswordService.resetPassword(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
