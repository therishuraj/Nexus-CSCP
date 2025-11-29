package com.nexus.investment_service.utils;

import com.nexus.investment_service.model.FundingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static com.nexus.investment_service.utils.Constants.*;

public class Validation {
    // Validates ownership of a funding request by funderId
    public static void validateOwnership(FundingRequest request, String funderId) {
        if (!request.getFunderId().equals(funderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not authorized to update this funding request.");
        }
    }

    // Validates the funding request is in OPEN status for update/invest
    public static void validateRequestOpen(FundingRequest request, String action) {
        if (!STATUS_OPEN.equals(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot " + action + " a funding request that is not OPEN (Current status: " + request.getStatus() + ").");
        }
    }

    // Validates walletAdjustment (negative) and remaining requirement
    public static void validateInvestment(FundingRequest request, double walletAdjustment) {
        if (walletAdjustment >= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "walletAdjustment must be negative for deduction.");
        }
        double remaining = request.getRequiredAmount() - request.getCurrentFunded();
        if (Math.abs(walletAdjustment) > remaining) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Investment exceeds remaining required amount. Remaining: " + remaining);
        }
    }
}
