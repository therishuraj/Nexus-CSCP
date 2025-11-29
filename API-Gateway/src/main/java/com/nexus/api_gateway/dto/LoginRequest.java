package com.nexus.api_gateway.dto;

/**
 * Data Transfer Object for user login request.
 * Replaces Lombok's @Data.
 */
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}