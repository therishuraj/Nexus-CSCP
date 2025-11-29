package com.nexus.user_service.dto.request;

public class UserValidationRequestDTO {
    
    private String email;
    private String password;
    
    // Default constructor
    public UserValidationRequestDTO() {}
    
    // Constructor with all fields
    public UserValidationRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "UserValidationRequestDTO{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
