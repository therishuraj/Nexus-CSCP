package com.nexus.user_service.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class UserCreateRequestDTO {
    
    private String name;
    private String email;
    private String password;
    private List<String> roles;
    private BigDecimal walletBalance; // Optional, defaults to 0 if not provided
    
    // Default constructor
    public UserCreateRequestDTO() {}
    
    // Constructor with all fields
    public UserCreateRequestDTO(String name, String email, String password, List<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.walletBalance = null; // Will default to 0 in service layer
    }
    
    // Constructor with wallet balance
    public UserCreateRequestDTO(String name, String email, String password, List<String> roles, BigDecimal walletBalance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.walletBalance = walletBalance;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
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
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public BigDecimal getWalletBalance() {
        return walletBalance;
    }
    
    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
    
    @Override
    public String toString() {
        return "UserCreateRequestDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
