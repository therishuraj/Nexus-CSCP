package com.payment.paymentservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class Payment {

    @Id
    private String id;

    private String externalUserId;
    private double amount;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    // Default constructor
    public Payment() {}

    // All args constructor
    public Payment(String id, String externalUserId, double amount, PaymentStatus status, Instant createdAt, Instant updatedAt, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.id = id;
        this.externalUserId = externalUserId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }   

    // Builder pattern
    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private String id;
        private String externalUserId;
        private double amount;
        private PaymentStatus status;
        private Instant createdAt;
        private Instant updatedAt;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String razorpaySignature;

        public PaymentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder externalUserId(String externalUserId) {
            this.externalUserId = externalUserId;
            return this;
        }

        public PaymentBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public PaymentBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PaymentBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PaymentBuilder razorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
            return this;
        }

        public PaymentBuilder razorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
            return this;
        }

        public PaymentBuilder razorpaySignature(String razorpaySignature) {
            this.razorpaySignature = razorpaySignature;
            return this;
        }

        public Payment build() {
            return new Payment(id, externalUserId, amount, status, createdAt, updatedAt, razorpayOrderId, razorpayPaymentId, razorpaySignature);
        }
    }
}
