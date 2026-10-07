package com.tecverse.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean success;
    private String message;
    private String accessToken;
    private String token;
    private Map<String, Object> user;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static LoginResponse success(String message, String token, Map<String, Object> user) {
        LoginResponse resp = new LoginResponse(true, message);
        resp.setAccessToken(token);
        resp.setToken(token);
        resp.setUser(user);
        return resp;
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
}
