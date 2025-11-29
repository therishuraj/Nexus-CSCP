package com.nexus.user_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDTO {
    
    private String id;
    private String name;
    private String email;
    private List<String> roles;
    private BigDecimal walletBalance;
    private List<String> fundingRequestIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public UserResponseDTO() {}
    
    // Constructor with all fields
    public UserResponseDTO(String id, String name, String email, List<String> roles, 
                          BigDecimal walletBalance, List<String> fundingRequestIds,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.walletBalance = walletBalance;
        this.fundingRequestIds = fundingRequestIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public List<String> getFundingRequestIds() {
        return fundingRequestIds;
    }
    
    public void setFundingRequestIds(List<String> fundingRequestIds) {
        this.fundingRequestIds = fundingRequestIds;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", walletBalance=" + walletBalance +
                ", fundingRequestIds=" + fundingRequestIds +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
