package com.payment.paymentservice.dto;

// @Data
// @AllArgsConstructor
public class WalletAdjustmentRequest {

    private double walletAdjustment;

    // Default constructor (this was missing and causing the error)
    public WalletAdjustmentRequest() {}

    // All args constructor
    public WalletAdjustmentRequest(double walletAdjustment) {
        this.walletAdjustment = walletAdjustment;
    }

    // Getter
    public double getWalletAdjustment() {
        return walletAdjustment;
    }

    // Setter
    public void setWalletAdjustment(double walletAdjustment) {
        this.walletAdjustment = walletAdjustment;
    }
}
