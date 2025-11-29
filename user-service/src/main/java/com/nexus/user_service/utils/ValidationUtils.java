package com.nexus.user_service.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Name validation pattern (letters, spaces, hyphens, apostrophes)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s\\-']{2,50}$"
    );
    
    // Phone validation pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    /**
     * Validate email format
     * @param email email to validate
     * @return true if email is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate name format
     * @param name name to validate
     * @return true if name is valid
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone phone number to validate
     * @return true if phone is valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Check if string is null or empty
     * @param str string to check
     * @return true if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     * @param str string to check
     * @return true if string is not null and not empty
     */
    public static boolean isNotNullAndNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    
    /**
     * Sanitize input by removing potentially harmful characters
     * @param input input string to sanitize
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potentially harmful characters
                   .replaceAll("\\s+", " ");   // Replace multiple spaces with single space
    }
    
    /**
     * Validate user role
     * @param role role to validate
     * @return true if role is valid
     */
    public static boolean isValidRole(String role) {
        if (isNullOrEmpty(role)) {
            return false;
        }
        
        String[] validRoles = {"ADMIN", "SUPPLIER", "FUNDER", "INVESTOR"};
        String upperRole = role.toUpperCase().trim();
        
        for (String validRole : validRoles) {
            if (validRole.equals(upperRole)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate string length
     * @param str string to validate
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if string length is within bounds
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate ID format (MongoDB ObjectId or similar)
     * @param id ID to validate
     * @return true if ID format is valid
     */
    public static boolean isValidId(String id) {
        if (isNullOrEmpty(id)) {
            return false;
        }
        
        // Basic validation for MongoDB ObjectId (24 hex characters)
        return id.matches("^[a-fA-F0-9]{24}$");
    }
    
    /**
     * Validate wallet balance amount
     * @param walletBalance wallet balance to validate
     * @return true if wallet balance is valid
     */
    public static boolean isValidWalletBalance(BigDecimal walletBalance) {
        if (walletBalance == null) {
            return true; // null is acceptable (will default to 0)
        }
        
        // Wallet balance must be non-negative and reasonable
        return walletBalance.compareTo(BigDecimal.ZERO) >= 0 && 
               walletBalance.compareTo(new BigDecimal("999999999.99")) <= 0;
    }
    
    /**
     * Get validation error message for wallet balance
     * @param walletBalance wallet balance to validate
     * @return error message or null if valid
     */
    public static String getWalletBalanceValidationError(BigDecimal walletBalance) {
        if (walletBalance != null) {
            if (walletBalance.compareTo(BigDecimal.ZERO) < 0) {
                return "Wallet balance cannot be negative";
            }
            if (walletBalance.compareTo(new BigDecimal("999999999.99")) > 0) {
                return "Wallet balance cannot exceed 999,999,999.99";
            }
            // Check decimal places (max 2 decimal places for currency)
            if (walletBalance.scale() > 2) {
                return "Wallet balance cannot have more than 2 decimal places";
            }
        }
        return null;
    }
    
    /**
     * Get validation error message for email
     * @param email email to validate
     * @return error message or null if valid
     */
    public static String getEmailValidationError(String email) {
        if (isNullOrEmpty(email)) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }
    
    /**
     * Get validation error message for name
     * @param name name to validate
     * @return error message or null if valid
     */
    public static String getNameValidationError(String name) {
        if (isNullOrEmpty(name)) {
            return "Name is required";
        }
        if (!isValidName(name)) {
            return "Name can only contain letters, spaces, hyphens, and apostrophes (2-50 characters)";
        }
        return null;
    }
    
    /**
     * Validate UserCreateRequestDTO
     * @param request UserCreateRequestDTO to validate
     * @return error message or null if valid
     */
    public static String validateUserCreateRequest(com.nexus.user_service.dto.request.UserCreateRequestDTO request) {
        if (request == null) {
            return "Request body is required";
        }
        
        // Validate name
        String nameError = getNameValidationError(request.getName());
        if (nameError != null) {
            return nameError;
        }
        
        // Validate email
        String emailError = getEmailValidationError(request.getEmail());
        if (emailError != null) {
            return emailError;
        }
        
        // Validate password
        if (isNullOrEmpty(request.getPassword())) {
            return "Password is required";
        }
        if (!isValidLength(request.getPassword(), 6, 100)) {
            return "Password must be between 6 and 100 characters";
        }
        
        // Validate roles
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            return "At least one role is required";
        }
        for (String role : request.getRoles()) {
            if (!isValidRole(role)) {
                return "Invalid role: " + role + ". Valid roles are: ADMIN, SUPPLIER, FUNDER, INVESTOR";
            }
        }
        
        // Validate wallet balance if provided
        String walletError = getWalletBalanceValidationError(request.getWalletBalance());
        if (walletError != null) {
            return walletError;
        }
        
        return null;
    }
    
    /**
     * Validate UserUpdateRequestDTO
     * @param request UserUpdateRequestDTO to validate
     * @return error message or null if valid
     */
    public static String validateUserUpdateRequest(com.nexus.user_service.dto.request.UserUpdateRequestDTO request) {
        if (request == null) {
            return "Request body is required";
        }
        
        // At least one field should be provided
        if (isNullOrEmpty(request.getName()) && isNullOrEmpty(request.getEmail()) && 
            request.getWalletBalance() == null && request.getWalletAdjustment() == null && 
            request.getFundingRequestIds() == null) {
            return "At least one field (name, email, walletBalance, walletAdjustment, or fundingRequestIds) must be provided for update";
        }
        
        // Validate name if provided
        if (isNotNullAndNotEmpty(request.getName())) {
            String nameError = getNameValidationError(request.getName());
            if (nameError != null) {
                return nameError;
            }
        }
        
        // Validate email if provided
        if (isNotNullAndNotEmpty(request.getEmail())) {
            String emailError = getEmailValidationError(request.getEmail());
            if (emailError != null) {
                return emailError;
            }
        }
        
        // Validate wallet balance if provided
        if (request.getWalletBalance() != null) {
            String walletError = getWalletBalanceValidationError(request.getWalletBalance());
            if (walletError != null) {
                return walletError;
            }
        }
        
        return null;
    }
}
