package com.nexus.user_service.dto.request;

import java.math.BigDecimal;

public class WalletWithdrawRequestDTO {
    
    private BigDecimal amount;
    
    // Default constructor
    public WalletWithdrawRequestDTO() {}
    
    // Constructor with amount
    public WalletWithdrawRequestDTO(BigDecimal amount) {
        this.amount = amount;
    }
    
    // Getter and Setter
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "WalletWithdrawRequestDTO{" +
                "amount=" + amount +
                '}';
    }
}
