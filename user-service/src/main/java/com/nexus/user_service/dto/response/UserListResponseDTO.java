package com.nexus.user_service.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class UserListResponseDTO {
    
    private String id;
    private String name;
    private List<String> roles;
    private BigDecimal walletBalance;
    
    // Default constructor
    public UserListResponseDTO() {}
    
    // Constructor with all fields
    public UserListResponseDTO(String id, String name, List<String> roles, BigDecimal walletBalance) {
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.walletBalance = walletBalance;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
        return "UserListResponseDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
