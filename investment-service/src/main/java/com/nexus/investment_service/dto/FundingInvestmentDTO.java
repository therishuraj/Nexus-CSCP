// Java
package com.nexus.investment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;

/**
 * Investor sends a negative value to indicate wallet deduction.
 */
public class FundingInvestmentDTO {

    @NotBlank
    private String investorId;

    @Negative(message = "walletAdjustment must be a negative value for deduction.")
    @JsonProperty("walletAdjustment")
    private double walletAdjustment; // negative amount to deduct

    public String getInvestorId() { return investorId; }
    public void setInvestorId(String investorId) { this.investorId = investorId; }

    // Keep method name semantic (amount is absolute investment size)
    public double getWalletAdjustment() { return walletAdjustment; }
    public void setWalletAdjustment(double walletAdjustment) { this.walletAdjustment = walletAdjustment; }
}
