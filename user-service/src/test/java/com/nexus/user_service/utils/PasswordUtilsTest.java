package com.nexus.user_service.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("PasswordUtils Unit Tests")
class PasswordUtilsTest {

    @Test
    @DisplayName("Hash Password - Valid Input")
    void hashPassword_ValidInput_ReturnsHashedPassword() {
        // Given
        String password = "testPassword123";

        // When
        String hashedPassword = PasswordUtils.hashPassword(password);

        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEmpty();
        assertThat(hashedPassword).isNotEqualTo(password);
        assertThat(hashedPassword).hasSize(64); // SHA-256 produces 64 character hex string
    }

    @Test
    @DisplayName("Hash Password - Same Input Produces Same Hash")
    void hashPassword_SameInput_ProducesSameHash() {
        // Given
        String password = "consistentPassword";

        // When
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Hash Password - Different Inputs Produce Different Hashes")
    void hashPassword_DifferentInputs_ProduceDifferentHashes() {
        // Given
        String password1 = "password1";
        String password2 = "password2";

        // When
        String hash1 = PasswordUtils.hashPassword(password1);
        String hash2 = PasswordUtils.hashPassword(password2);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Hash Password With Salt - Valid Inputs")
    void hashPasswordWithSalt_ValidInputs_ReturnsHashedPassword() {
        // Given
        String password = "testPassword";
        String salt = "randomSalt";

        // When
        String hashedPassword = PasswordUtils.hashPasswordWithSalt(password, salt);

        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEmpty();
        assertThat(hashedPassword).isNotEqualTo(password);
        assertThat(hashedPassword).hasSize(64);
    }

    @Test
    @DisplayName("Hash Password With Salt - Same Inputs Produce Same Hash")
    void hashPasswordWithSalt_SameInputs_ProduceSameHash() {
        // Given
        String password = "testPassword";
        String salt = "testSalt";

        // When
        String hash1 = PasswordUtils.hashPasswordWithSalt(password, salt);
        String hash2 = PasswordUtils.hashPasswordWithSalt(password, salt);

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Hash Password With Salt - Different Salts Produce Different Hashes")
    void hashPasswordWithSalt_DifferentSalts_ProduceDifferentHashes() {
        // Given
        String password = "testPassword";
        String salt1 = "salt1";
        String salt2 = "salt2";

        // When
        String hash1 = PasswordUtils.hashPasswordWithSalt(password, salt1);
        String hash2 = PasswordUtils.hashPasswordWithSalt(password, salt2);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Verify Password - Correct Password Returns True")
    void verifyPassword_CorrectPassword_ReturnsTrue() {
        // Given
        String password = "testPassword123";
        String hash = PasswordUtils.hashPassword(password);

        // When
        boolean isValid = PasswordUtils.verifyPassword(password, hash);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Verify Password - Incorrect Password Returns False")
    void verifyPassword_IncorrectPassword_ReturnsFalse() {
        // Given
        String originalPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hash = PasswordUtils.hashPassword(originalPassword);

        // When
        boolean isValid = PasswordUtils.verifyPassword(wrongPassword, hash);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Verify Password With Salt - Correct Password And Salt Returns True")
    void verifyPasswordWithSalt_CorrectPasswordAndSalt_ReturnsTrue() {
        // Given
        String password = "testPassword";
        String salt = "testSalt";
        String hash = PasswordUtils.hashPasswordWithSalt(password, salt);

        // When
        boolean isValid = PasswordUtils.verifyPasswordWithSalt(password, hash, salt);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Verify Password With Salt - Wrong Password Returns False")
    void verifyPasswordWithSalt_WrongPassword_ReturnsFalse() {
        // Given
        String originalPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String salt = "testSalt";
        String hash = PasswordUtils.hashPasswordWithSalt(originalPassword, salt);

        // When
        boolean isValid = PasswordUtils.verifyPasswordWithSalt(wrongPassword, hash, salt);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Verify Password With Salt - Wrong Salt Returns False")
    void verifyPasswordWithSalt_WrongSalt_ReturnsFalse() {
        // Given
        String password = "testPassword";
        String correctSalt = "correctSalt";
        String wrongSalt = "wrongSalt";
        String hash = PasswordUtils.hashPasswordWithSalt(password, correctSalt);

        // When
        boolean isValid = PasswordUtils.verifyPasswordWithSalt(password, hash, wrongSalt);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Generate Salt - Returns Non-Empty Base64 String")
    void generateSalt_ReturnsNonEmptyBase64String() {
        // When
        String salt = PasswordUtils.generateSalt();

        // Then
        assertThat(salt).isNotNull();
        assertThat(salt).isNotEmpty();
        assertThat(salt).matches("^[A-Za-z0-9+/]*={0,2}$"); // Base64 pattern
    }

    @Test
    @DisplayName("Generate Salt - Different Calls Produce Different Salts")
    void generateSalt_DifferentCalls_ProduceDifferentSalts() {
        // When
        String salt1 = PasswordUtils.generateSalt();
        String salt2 = PasswordUtils.generateSalt();

        // Then
        assertThat(salt1).isNotEqualTo(salt2);
    }

    @Test
    @DisplayName("Is Valid Password - Strong Password Returns True")
    void isValidPassword_StrongPassword_ReturnsTrue() {
        // Given
        String strongPassword = "StrongP@ss1";

        // When
        boolean isValid = PasswordUtils.isValidPassword(strongPassword);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Is Valid Password - Null Password Returns False")
    void isValidPassword_NullPassword_ReturnsFalse() {
        // When
        boolean isValid = PasswordUtils.isValidPassword(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - Short Password Returns False")
    void isValidPassword_ShortPassword_ReturnsFalse() {
        // Given
        String shortPassword = "Short1!";

        // When
        boolean isValid = PasswordUtils.isValidPassword(shortPassword);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - No Uppercase Returns False")
    void isValidPassword_NoUppercase_ReturnsFalse() {
        // Given
        String password = "lowercase123!";

        // When
        boolean isValid = PasswordUtils.isValidPassword(password);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - No Lowercase Returns False")
    void isValidPassword_NoLowercase_ReturnsFalse() {
        // Given
        String password = "UPPERCASE123!";

        // When
        boolean isValid = PasswordUtils.isValidPassword(password);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - No Digit Returns False")
    void isValidPassword_NoDigit_ReturnsFalse() {
        // Given
        String password = "NoDigitsHere!";

        // When
        boolean isValid = PasswordUtils.isValidPassword(password);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - No Special Character Returns False")
    void isValidPassword_NoSpecialCharacter_ReturnsFalse() {
        // Given
        String password = "NoSpecialChar123";

        // When
        boolean isValid = PasswordUtils.isValidPassword(password);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - Empty String Returns False")
    void isValidPassword_EmptyString_ReturnsFalse() {
        // When
        boolean isValid = PasswordUtils.isValidPassword("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is Valid Password - All Special Characters Work")
    void isValidPassword_AllSpecialCharacters_Work() {
        // Given - Test different special characters
        String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "]", "{", "}", "|", ";", ":", ",", ".", "<", ">", "?"};

        for (String specialChar : specialChars) {
            String password = "ValidPass1" + specialChar;

            // When
            boolean isValid = PasswordUtils.isValidPassword(password);

            // Then
            assertThat(isValid).as("Password with special character '%s' should be valid", specialChar).isTrue();
        }
    }

    @Test
    @DisplayName("Hash Password - Edge Cases")
    void hashPassword_EdgeCases() {
        // Test empty string
        String emptyHash = PasswordUtils.hashPassword("");
        assertThat(emptyHash).isNotNull().hasSize(64);

        // Test single character
        String singleCharHash = PasswordUtils.hashPassword("a");
        assertThat(singleCharHash).isNotNull().hasSize(64);

        // Test very long password
        String longPassword = "a".repeat(1000);
        String longHash = PasswordUtils.hashPassword(longPassword);
        assertThat(longHash).isNotNull().hasSize(64);
    }
}
