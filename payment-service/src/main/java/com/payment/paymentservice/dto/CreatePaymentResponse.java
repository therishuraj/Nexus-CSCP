package com.payment.paymentservice.dto;

// @Data
// @AllArgsConstructor
public class CreatePaymentResponse {

    private String paymentId;
    private String externalUserId;
    private double amount;
    private String status;
    private String message;

    // Default constructor
    public CreatePaymentResponse() {}

    // All args constructor
    public CreatePaymentResponse(String paymentId, String externalUserId, double amount, String status, String message) {
        this.paymentId = paymentId;
        this.externalUserId = externalUserId;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }

    // Getters
    public String getPaymentId() {
        return paymentId;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
