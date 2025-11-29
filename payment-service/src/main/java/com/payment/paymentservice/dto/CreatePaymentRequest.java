package com.payment.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// @Data
public class CreatePaymentRequest {

    @NotBlank
    private String externalUserId;

    @Min(1)
    private double amount;   // in currency units

    // Default constructor
    public CreatePaymentRequest() {}

    // Constructor
    public CreatePaymentRequest(String externalUserId, double amount) {
        this.externalUserId = externalUserId;
        this.amount = amount;
    }

    // Getters
    public String getExternalUserId() {
        return externalUserId;
    }

    public double getAmount() {
        return amount;
    }

    // Setters
    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
