package com.payment.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// @Data
public class CreatePayoutRequest {

    @NotBlank
    private String externalUserId;

    @Min(1)
    private double amount;

    @NotBlank
    private String upiId;

    // Default constructor
    public CreatePayoutRequest() {}

    // Constructor
    public CreatePayoutRequest(String externalUserId, double amount, String upiId) {
        this.externalUserId = externalUserId;
        this.amount = amount;
        this.upiId = upiId;
    }

    // Getters
    public String getExternalUserId() {
        return externalUserId;
    }

    public double getAmount() {
        return amount;
    }

    public String getUpiId() {
        return upiId;
    }

    // Setters
    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
