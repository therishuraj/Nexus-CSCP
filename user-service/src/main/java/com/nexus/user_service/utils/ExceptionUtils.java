package com.nexus.user_service.utils;

import java.math.BigDecimal;

/**
 * Utility class for centralized exception handling and custom exceptions
 */
public class ExceptionUtils {
    
    /**
     * Custom exception for insufficient funds scenarios
     */
    public static class InsufficientFundsException extends RuntimeException {
        
        private final BigDecimal currentBalance;
        private final BigDecimal requestedAmount;
        private final BigDecimal shortfall;
        
        public InsufficientFundsException(BigDecimal currentBalance, BigDecimal requestedAmount) {
            super(String.format("Insufficient funds. Current balance: %s, Requested deduction: %s", 
                  currentBalance, requestedAmount.abs()));
            this.currentBalance = currentBalance;
            this.requestedAmount = requestedAmount.abs();
            this.shortfall = this.requestedAmount.subtract(currentBalance);
        }
        
        public InsufficientFundsException(String message, BigDecimal currentBalance, BigDecimal requestedAmount) {
            super(message);
            this.currentBalance = currentBalance;
            this.requestedAmount = requestedAmount.abs();
            this.shortfall = this.requestedAmount.subtract(currentBalance);
        }
        
        public BigDecimal getCurrentBalance() {
            return currentBalance;
        }
        
        public BigDecimal getRequestedAmount() {
            return requestedAmount;
        }
        
        public BigDecimal getShortfall() {
            return shortfall;
        }
    }
    
    /**
     * Custom exception for user not found scenarios
     */
    public static class UserNotFoundException extends RuntimeException {
        
        private final String userId;
        
        public UserNotFoundException(String userId) {
            super(String.format("User not found with ID: %s", userId));
            this.userId = userId;
        }
        
        public UserNotFoundException(String message, String userId) {
            super(message);
            this.userId = userId;
        }
        
        public String getUserId() {
            return userId;
        }
    }
    
    /**
     * Custom exception for duplicate email scenarios
     */
    public static class DuplicateEmailException extends RuntimeException {
        
        private final String email;
        
        public DuplicateEmailException(String email) {
            super(String.format("User already exists with email: %s", email));
            this.email = email;
        }
        
        public DuplicateEmailException(String message, String email) {
            super(message);
            this.email = email;
        }
        
        public String getEmail() {
            return email;
        }
    }
    
    /**
     * Custom exception for invalid user data scenarios
     */
    public static class InvalidUserDataException extends RuntimeException {
        
        private final String field;
        private final String value;
        
        public InvalidUserDataException(String field, String value) {
            super(String.format("Invalid data for field '%s': %s", field, value));
            this.field = field;
            this.value = value;
        }
        
        public InvalidUserDataException(String message, String field, String value) {
            super(message);
            this.field = field;
            this.value = value;
        }
        
        public String getField() {
            return field;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Custom exception for wallet operation scenarios
     */
    public static class WalletOperationException extends RuntimeException {
        
        private final String operation;
        private final BigDecimal amount;
        
        public WalletOperationException(String operation, BigDecimal amount) {
            super(String.format("Wallet operation failed - Operation: %s, Amount: %s", operation, amount));
            this.operation = operation;
            this.amount = amount;
        }
        
        public WalletOperationException(String message, String operation, BigDecimal amount) {
            super(message);
            this.operation = operation;
            this.amount = amount;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
    }
    
    // Factory methods for creating exceptions
    
    /**
     * Create InsufficientFundsException
     */
    public static InsufficientFundsException insufficientFunds(BigDecimal currentBalance, BigDecimal requestedAmount) {
        return new InsufficientFundsException(currentBalance, requestedAmount);
    }
    
    /**
     * Create UserNotFoundException
     */
    public static UserNotFoundException userNotFound(String userId) {
        return new UserNotFoundException(userId);
    }
    
    /**
     * Create DuplicateEmailException
     */
    public static DuplicateEmailException duplicateEmail(String email) {
        return new DuplicateEmailException(email);
    }
    
    /**
     * Create InvalidUserDataException
     */
    public static InvalidUserDataException invalidUserData(String field, String value) {
        return new InvalidUserDataException(field, value);
    }
    
    /**
     * Create WalletOperationException
     */
    public static WalletOperationException walletOperationFailed(String operation, BigDecimal amount) {
        return new WalletOperationException(operation, amount);
    }
}
