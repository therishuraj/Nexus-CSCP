package com.razz.orderservice.dto;

import java.math.BigDecimal;

public class UserUpdateRequest {
    private BigDecimal walletAdjustment;

    public UserUpdateRequest() {}

    public UserUpdateRequest(BigDecimal walletAdjustment) {
        this.walletAdjustment = walletAdjustment;
    }

    public BigDecimal getWalletAdjustment() {
        return walletAdjustment;
    }

    public void setWalletAdjustment(BigDecimal walletAdjustment) {
        this.walletAdjustment = walletAdjustment;
    }
}
