package com.nexus.user_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Hash password using SHA-256
     * @param password plain text password
     * @return hashed password
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Hash password with salt for better security
     * @param password plain text password
     * @param salt salt string
     * @return hashed password with salt
     */
    public static String hashPasswordWithSalt(String password, String salt) {
        return hashPassword(password + salt);
    }
    
    /**
     * Verify password against hash
     * @param password plain text password
     * @param hash stored hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String hash) {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(hash);
    }
    
    /**
     * Verify password with salt against hash
     * @param password plain text password
     * @param hash stored hash
     * @param salt salt used during hashing
     * @return true if password matches
     */
    public static boolean verifyPasswordWithSalt(String password, String hash, String salt) {
        String hashedPassword = hashPasswordWithSalt(password, salt);
        return hashedPassword.equals(hash);
    }
    
    /**
     * Generate random salt
     * @return base64 encoded salt
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Validate password strength
     * @param password password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    /**
     * Convert byte array to hex string
     * @param bytes byte array
     * @return hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
