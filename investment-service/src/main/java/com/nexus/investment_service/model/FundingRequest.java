package com.nexus.investment_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "funding_requests")
public class FundingRequest {
    @Id
    private String id;

    private String title;
    private double requiredAmount;
    private double currentFunded;
    private String funderId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    // Map of investorId -> total invested amount
    private Map<String, Double> investorAmounts;

    // Total committed return (gross) to distribute to investors upon success
    private double committedReturnAmount;

    // Detailed description of the funding request
    private String description;

    // Flag indicating whether returns have been distributed
    private boolean returnDistributed;

    public FundingRequest() {
        // no-args constructor for framework
    }

    public FundingRequest(String title, double requiredAmount, double committedReturnAmount, String description, LocalDateTime deadline, String funderId) {
        this.title = title;
        this.requiredAmount = requiredAmount;
        this.committedReturnAmount = committedReturnAmount;
        this.description = description;
        this.deadline = deadline;
        this.funderId = funderId;
        this.currentFunded = 0.0;
        this.investorAmounts = new HashMap<>();
        this.returnDistributed = false;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getRequiredAmount() { return requiredAmount; }
    public void setRequiredAmount(double requiredAmount) { this.requiredAmount = requiredAmount; }

    public double getCurrentFunded() { return currentFunded; }
    public void setCurrentFunded(double currentFunded) { this.currentFunded = currentFunded; }

    public String getFunderId() { return funderId; }
    public void setFunderId(String funderId) { this.funderId = funderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Map<String, Double> getInvestorAmounts() { return investorAmounts; }
    public void setInvestorAmounts(Map<String, Double> investorAmounts) { this.investorAmounts = investorAmounts; }

    public double getCommittedReturnAmount() { return committedReturnAmount; }
    public void setCommittedReturnAmount(double committedReturnAmount) { this.committedReturnAmount = committedReturnAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isReturnDistributed() { return returnDistributed; }
    public void setReturnDistributed(boolean returnDistributed) { this.returnDistributed = returnDistributed; }

    // Convenience: update or insert investor amount (accumulate multiple investments)
    public void updateInvestorAmount(String investorId, double amount) {
        if (this.investorAmounts == null) {
            this.investorAmounts = new HashMap<>();
        }
        this.investorAmounts.merge(investorId, amount, Double::sum);
    }
}
