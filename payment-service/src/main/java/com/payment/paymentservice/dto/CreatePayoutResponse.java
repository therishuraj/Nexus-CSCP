package com.payment.paymentservice.dto;

// @Data
// @AllArgsConstructor
public class CreatePayoutResponse {

    private String payoutId;
    private String externalUserId;
    private double amount;
    private String upiId;
    private String status;
    private String message;

    // Default constructor
    public CreatePayoutResponse() {}

    // All args constructor
    public CreatePayoutResponse(String payoutId, String externalUserId, double amount, String upiId, String status, String message) {
        this.payoutId = payoutId;
        this.externalUserId = externalUserId;
        this.amount = amount;
        this.upiId = upiId;
        this.status = status;
        this.message = message;
    }

    // Getters
    public String getPayoutId() {
        return payoutId;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public double getAmount() {
        return amount;
    }

    public String getUpiId() {
        return upiId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
