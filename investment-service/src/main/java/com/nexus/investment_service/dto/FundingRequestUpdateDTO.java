package com.nexus.investment_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for updating an existing FundingRequest.
 * Fields are optional for partial updates, but validation still applies if present.
 */
public class FundingRequestUpdateDTO {

    @Size(min = 1, message = "Title cannot be empty")
    private String title;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @Min(value = 0, message = "Committed return amount must be non-negative if provided")
    private Double committedReturnAmount;

    private String description; // optional

    // --- Getters and Setters ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Double getCommittedReturnAmount() {
        return committedReturnAmount;
    }

    public void setCommittedReturnAmount(Double committedReturnAmount) {
        this.committedReturnAmount = committedReturnAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}