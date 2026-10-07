package com.tecverse.backend.dto;

public class LoginRequest {

    private String email;
    private String identifier;
    private String mobile;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Resolves the login username/email/identifier from any provided field.
     */
    public String getEffectiveIdentifier() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        if (identifier != null && !identifier.trim().isEmpty()) {
            return identifier.trim();
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            return mobile.trim();
        }
        return null;
    }
}
