package com.nexus.investment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class UserUpdateRequestDTO {
    private String name;
    private String email;
    @JsonProperty("walletAdjustment")
    private BigDecimal walletAdjustment;
    private List<String> fundingRequestIds;

    public UserUpdateRequestDTO() {}

    public UserUpdateRequestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserUpdateRequestDTO(String name, String email, BigDecimal walletAdjustment) {
        this.name = name;
        this.email = email;
        this.walletAdjustment = walletAdjustment;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getWalletAdjustment() { return walletAdjustment; }
    public void setWalletAdjustment(BigDecimal walletAdjustment) { this.walletAdjustment = walletAdjustment; }

    public List<String> getFundingRequestIds() { return fundingRequestIds; }
    public void setFundingRequestIds(List<String> fundingRequestIds) { this.fundingRequestIds = fundingRequestIds; }
}
