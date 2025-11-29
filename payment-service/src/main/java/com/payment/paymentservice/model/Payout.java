package com.payment.paymentservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payouts")
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class Payout {

    @Id
    private String id;

    private String externalUserId;
    private double amount;
    private String upiId;
    private PayoutStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    // Default constructor
    public Payout() {}

    // All args constructor
    public Payout(String id, String externalUserId, double amount, String upiId, PayoutStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.externalUserId = externalUserId;
        this.amount = amount;
        this.upiId = upiId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getUpiId() {
        return upiId;
    }

    public PayoutStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public void setStatus(PayoutStatus status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static PayoutBuilder builder() {
        return new PayoutBuilder();
    }

    public static class PayoutBuilder {
        private String id;
        private String externalUserId;
        private double amount;
        private String upiId;
        private PayoutStatus status;
        private Instant createdAt;
        private Instant updatedAt;

        public PayoutBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PayoutBuilder externalUserId(String externalUserId) {
            this.externalUserId = externalUserId;
            return this;
        }

        public PayoutBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public PayoutBuilder upiId(String upiId) {
            this.upiId = upiId;
            return this;
        }

        public PayoutBuilder status(PayoutStatus status) {
            this.status = status;
            return this;
        }

        public PayoutBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PayoutBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Payout build() {
            return new Payout(id, externalUserId, amount, upiId, status, createdAt, updatedAt);
        }
    }
}
