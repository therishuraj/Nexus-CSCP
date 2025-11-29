package com.nexus.user_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String passwordHash;
    
    private List<String> roles;
    
    private BigDecimal walletBalance;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<String> fundingRequestIds;
    
    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.walletBalance = BigDecimal.ZERO;
        this.fundingRequestIds = new ArrayList<>();
    }
    
    // Constructor with required fields
    public User(String name, String email, String passwordHash, List<String> roles) {
        this();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
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
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
        this.updatedAt = LocalDateTime.now();
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
    
    public BigDecimal getWalletBalance() {
        return walletBalance;
    }
    
    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<String> getFundingRequestIds() {
        return fundingRequestIds;
    }
    
    public void setFundingRequestIds(List<String> fundingRequestIds) {
        this.fundingRequestIds = fundingRequestIds != null ? fundingRequestIds : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for managing funding request IDs
    public void addFundingRequestId(String fundingRequestId) {
        if (this.fundingRequestIds == null) {
            this.fundingRequestIds = new ArrayList<>();
        }
        if (fundingRequestId != null && !this.fundingRequestIds.contains(fundingRequestId)) {
            this.fundingRequestIds.add(fundingRequestId);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public boolean removeFundingRequestId(String fundingRequestId) {
        if (this.fundingRequestIds != null && fundingRequestId != null) {
            boolean removed = this.fundingRequestIds.remove(fundingRequestId);
            if (removed) {
                this.updatedAt = LocalDateTime.now();
            }
            return removed;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "User{" +
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
