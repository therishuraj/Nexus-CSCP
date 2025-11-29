package com.nexus.user_service.dto.request;

import java.math.BigDecimal;

public class WalletDepositRequestDTO {
    
    private BigDecimal amount;
    
    // Default constructor
    public WalletDepositRequestDTO() {}
    
    // Constructor with amount
    public WalletDepositRequestDTO(BigDecimal amount) {
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
        return "WalletDepositRequestDTO{" +
                "amount=" + amount +
                '}';
    }
}
