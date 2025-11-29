package com.payment.paymentservice.dto;

public class PaymentInitResponse {

    private String paymentId;
    private String status;
    private String razorpayOrderId;
    private String razorpayKey;
    private double amount;
    private String currency;
    private String checkoutUrl;

    // Private constructor for builder
    private PaymentInitResponse(Builder builder) {
        this.paymentId = builder.paymentId;
        this.status = builder.status;
        this.razorpayOrderId = builder.razorpayOrderId;
        this.razorpayKey = builder.razorpayKey;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.checkoutUrl = builder.checkoutUrl;
    }

    public PaymentInitResponse() {
    }

    // Getters
    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public String getRazorpayKey() {
        return razorpayKey;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String paymentId;
        private String status;
        private String razorpayOrderId;
        private String razorpayKey;
        private double amount;
        private String currency;
        private String checkoutUrl;

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder razorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
            return this;
        }

        public Builder razorpayKey(String razorpayKey) {
            this.razorpayKey = razorpayKey;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder checkoutUrl(String checkoutUrl) {
            this.checkoutUrl = checkoutUrl;
            return this;
        }

        public PaymentInitResponse build() {
            return new PaymentInitResponse(this);
        }
    }
}
