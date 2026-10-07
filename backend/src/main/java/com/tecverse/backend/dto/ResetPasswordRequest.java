package com.tecverse.backend.dto;

public class ResetPasswordRequest {

    private String email;
    private String identifier;
    private String otp;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequest() {}

    public String getEffectiveEmailOrIdentifier() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        if (identifier != null && !identifier.trim().isEmpty()) {
            return identifier.trim();
        }
        return "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
