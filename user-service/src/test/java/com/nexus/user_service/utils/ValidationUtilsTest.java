package com.nexus.user_service.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ValidationUtils
 * These tests verify validation logic without external dependencies
 */
@ActiveProfiles("test")
class ValidationUtilsTest {

    @Test
    @DisplayName("Should validate correct email format")
    void shouldValidateCorrectEmailFormat() {
        // Given
        String validEmail = "test@example.com";
        
        // When
        boolean result = ValidationUtils.isValidEmail(validEmail);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmailFormat() {
        // Given
        String invalidEmail = "invalid-email";
        
        // When
        boolean result = ValidationUtils.isValidEmail(invalidEmail);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject null email")
    void shouldRejectNullEmail() {
        // When
        boolean result = ValidationUtils.isValidEmail(null);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject empty email")
    void shouldRejectEmptyEmail() {
        // When
        boolean result = ValidationUtils.isValidEmail("");
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should validate MongoDB ObjectId format")
    void shouldValidateMongoDbObjectIdFormat() {
        // Given
        String validId = "507f1f77bcf86cd799439011";
        
        // When
        boolean result = ValidationUtils.isValidId(validId);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid ObjectId format")
    void shouldRejectInvalidObjectIdFormat() {
        // Given
        String invalidId = "invalid-id";
        
        // When
        boolean result = ValidationUtils.isValidId(invalidId);
        
        // Then
        assertThat(result).isFalse();
    }
}
