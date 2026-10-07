package com.tecverse.backend.dto;

public class VerifyOtpRequest {

    private String email;
    private String identifier;
    private String otp;

    public VerifyOtpRequest() {}

    public VerifyOtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

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
}
