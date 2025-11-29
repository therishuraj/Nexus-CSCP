package com.nexus.user_service.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class UserUpdateRequestDTO {
    
    private String name;
    private String email;
    private BigDecimal walletBalance; // Optional field for wallet updates (direct set)
    private BigDecimal walletAdjustment; // Optional field for wallet adjustments (+/- amounts)
    private List<String> fundingRequestIds; // Optional field for funding request updates
    
    // Default constructor
    public UserUpdateRequestDTO() {}
    
    // Constructor with name and email
    public UserUpdateRequestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Constructor with all fields
    public UserUpdateRequestDTO(String name, String email, BigDecimal walletBalance) {
        this.name = name;
        this.email = email;
        this.walletBalance = walletBalance;
    }
    
    // Constructor with all fields including funding request IDs
    public UserUpdateRequestDTO(String name, String email, BigDecimal walletBalance, List<String> fundingRequestIds) {
        this.name = name;
        this.email = email;
        this.walletBalance = walletBalance;
        this.fundingRequestIds = fundingRequestIds;
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
    
    public BigDecimal getWalletBalance() {
        return walletBalance;
    }
    
    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
    
    public BigDecimal getWalletAdjustment() {
        return walletAdjustment;
    }
    
    public void setWalletAdjustment(BigDecimal walletAdjustment) {
        this.walletAdjustment = walletAdjustment;
    }
    
    public List<String> getFundingRequestIds() {
        return fundingRequestIds;
    }
    
    public void setFundingRequestIds(List<String> fundingRequestIds) {
        this.fundingRequestIds = fundingRequestIds;
    }
    
    @Override
    public String toString() {
        return "UserUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", walletBalance=" + walletBalance +
                ", walletAdjustment=" + walletAdjustment +
                ", fundingRequestIds=" + fundingRequestIds +
                '}';
    }
}
