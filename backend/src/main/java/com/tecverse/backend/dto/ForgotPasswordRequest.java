package com.tecverse.backend.dto;

public class ForgotPasswordRequest {

    private String email;
    private String identifier;
    private String mobile;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEffectiveEmailOrIdentifier() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        if (identifier != null && !identifier.trim().isEmpty()) {
            return identifier.trim();
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            return mobile.trim();
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
