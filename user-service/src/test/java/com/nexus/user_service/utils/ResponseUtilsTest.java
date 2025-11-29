package com.nexus.user_service.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DisplayName("ResponseUtils Unit Tests")
class ResponseUtilsTest {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    @DisplayName("Success With Data - Valid Inputs")
    void success_WithData_ValidInputs_ReturnsSuccessResponse() {
        // Given
        String message = "Operation completed successfully";
        List<String> data = Arrays.asList("item1", "item2", "item3");

        // When
        Map<String, Object> response = ResponseUtils.success(message, data);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo(message);
        assertThat(response.get("data")).isEqualTo(data);
        assertThat(response).containsKey("timestamp");
        assertThat(response.get("timestamp")).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Success With Data - Null Data")
    void success_WithData_NullData_ReturnsSuccessResponse() {
        // Given
        String message = "Operation completed";

        // When
        Map<String, Object> response = ResponseUtils.success(message, null);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo(message);
        assertThat(response.get("data")).isNull();
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Success Without Data - Valid Message")
    void success_WithoutData_ValidMessage_ReturnsSuccessResponse() {
        // Given
        String message = "Operation completed successfully";

        // When
        Map<String, Object> response = ResponseUtils.success(message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo(message);
        assertThat(response).doesNotContainKey("data");
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Error - Basic Error Message")
    void error_BasicErrorMessage_ReturnsErrorResponse() {
        // Given
        String errorMessage = "Something went wrong";

        // When
        Map<String, Object> response = ResponseUtils.error(errorMessage);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo(errorMessage);
        assertThat(response).containsKey("timestamp");
        assertThat(response).doesNotContainKey("details");
    }

    @Test
    @DisplayName("Error With Details - Error Message And Details")
    void error_WithDetails_ErrorMessageAndDetails_ReturnsErrorResponse() {
        // Given
        String errorMessage = "Validation failed";
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("field1", "Field is required");
        errorDetails.put("field2", "Invalid format");

        // When
        Map<String, Object> response = ResponseUtils.error(errorMessage, errorDetails);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo(errorMessage);
        assertThat(response.get("details")).isEqualTo(errorDetails);
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Validation Error - Validation Errors Map")
    void validationError_ValidationErrorsMap_ReturnsValidationErrorResponse() {
        // Given
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("email", "Email is required");
        validationErrors.put("name", "Name must be at least 2 characters");

        // When
        Map<String, Object> response = ResponseUtils.validationError(validationErrors);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo("Validation failed");
        assertThat(response.get("validationErrors")).isEqualTo(validationErrors);
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Unauthorized - Default Message")
    void unauthorized_DefaultMessage_ReturnsUnauthorizedResponse() {
        // When
        Map<String, Object> response = ResponseUtils.unauthorized();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo("Unauthorized access");
        assertThat(response.get("code")).isEqualTo("UNAUTHORIZED");
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Unauthorized - Custom Message")
    void unauthorized_CustomMessage_ReturnsUnauthorizedResponse() {
        // Given
        String customMessage = "Invalid token provided";

        // When
        Map<String, Object> response = ResponseUtils.unauthorized(customMessage);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo(customMessage);
        assertThat(response.get("code")).isEqualTo("UNAUTHORIZED");
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Not Found - Resource Name")
    void notFound_ResourceName_ReturnsNotFoundResponse() {
        // Given
        String resource = "User";

        // When
        Map<String, Object> response = ResponseUtils.notFound(resource);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo("User not found");
        assertThat(response.get("code")).isEqualTo("NOT_FOUND");
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Conflict - Conflict Message")
    void conflict_ConflictMessage_ReturnsConflictResponse() {
        // Given
        String conflictMessage = "Email already exists";

        // When
        Map<String, Object> response = ResponseUtils.conflict(conflictMessage);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("error")).isEqualTo(conflictMessage);
        assertThat(response.get("code")).isEqualTo("CONFLICT");
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Paginated - Valid Pagination Data")
    void paginated_ValidPaginationData_ReturnsPaginatedResponse() {
        // Given
        List<String> data = Arrays.asList("item1", "item2", "item3");
        int page = 1;
        int size = 10;
        long total = 25;

        // When
        Map<String, Object> response = ResponseUtils.paginated(data, page, size, total);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("data")).isEqualTo(data);
        assertThat(response).containsKey("pagination");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> pagination = (Map<String, Object>) response.get("pagination");
        assertThat(pagination.get("page")).isEqualTo(page);
        assertThat(pagination.get("size")).isEqualTo(size);
        assertThat(pagination.get("total")).isEqualTo(total);
        assertThat(pagination.get("totalPages")).isEqualTo(3); // Math.ceil(25/10) = 3
        
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Paginated - Edge Case Total Pages Calculation")
    void paginated_EdgeCaseTotalPagesCalculation_ReturnsCorrectTotalPages() {
        // Given - Exact division
        List<String> data = Arrays.asList("item1", "item2");
        int page = 1;
        int size = 10;
        long total = 20; // Should result in exactly 2 pages

        // When
        Map<String, Object> response = ResponseUtils.paginated(data, page, size, total);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> pagination = (Map<String, Object>) response.get("pagination");
        assertThat(pagination.get("totalPages")).isEqualTo(2); // 20/10 = 2
    }

    @Test
    @DisplayName("Paginated - Zero Total Items")
    void paginated_ZeroTotalItems_ReturnsZeroTotalPages() {
        // Given
        List<String> data = Arrays.asList();
        int page = 1;
        int size = 10;
        long total = 0;

        // When
        Map<String, Object> response = ResponseUtils.paginated(data, page, size, total);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> pagination = (Map<String, Object>) response.get("pagination");
        assertThat(pagination.get("totalPages")).isEqualTo(0); // Math.ceil(0/10) = 0
    }

    @Test
    @DisplayName("Login Success - Token And User ID")
    void loginSuccess_TokenAndUserId_ReturnsLoginResponse() {
        // Given
        String token = "jwt.token.here";
        String userId = "user123";

        // When
        Map<String, Object> response = ResponseUtils.loginSuccess(token, userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo("Login successful");
        assertThat(response.get("token")).isEqualTo(token);
        assertThat(response.get("userId")).isEqualTo(userId);
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Deleted - Resource Name")
    void deleted_ResourceName_ReturnsDeletionResponse() {
        // Given
        String resource = "User";

        // When
        Map<String, Object> response = ResponseUtils.deleted(resource);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo("User deleted successfully");
        assertThat(response.get("deleted")).isEqualTo(true);
        assertThat(response).containsKey("timestamp");
    }

    @Test
    @DisplayName("Timestamp Format - Consistent Format Across Methods")
    void timestampFormat_ConsistentFormatAcrossMethods_MatchesExpectedPattern() {
        // Given
        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        Map<String, Object> successResponse = ResponseUtils.success("test");
        Map<String, Object> errorResponse = ResponseUtils.error("test");
        
        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        String successTimestamp = (String) successResponse.get("timestamp");
        String errorTimestamp = (String) errorResponse.get("timestamp");
        
        // Verify format matches expected pattern
        assertThat(successTimestamp).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
        assertThat(errorTimestamp).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
        
        // Verify timestamps are within reasonable time range
        LocalDateTime successTime = LocalDateTime.parse(successTimestamp, TIMESTAMP_FORMAT);
        LocalDateTime errorTime = LocalDateTime.parse(errorTimestamp, TIMESTAMP_FORMAT);
        
        assertThat(successTime).isBetween(beforeCall.minusSeconds(1), afterCall.plusSeconds(1));
        assertThat(errorTime).isBetween(beforeCall.minusSeconds(1), afterCall.plusSeconds(1));
    }

    @Test
    @DisplayName("Response Structure - All Methods Return Consistent Structure")
    void responseStructure_AllMethods_ReturnConsistentStructure() {
        // When
        Map<String, Object> successResponse = ResponseUtils.success("test");
        Map<String, Object> errorResponse = ResponseUtils.error("test");
        Map<String, Object> unauthorizedResponse = ResponseUtils.unauthorized();
        Map<String, Object> notFoundResponse = ResponseUtils.notFound("Resource");

        // Then - All responses should have success field and timestamp
        assertThat(successResponse).containsKey("success").containsKey("timestamp");
        assertThat(errorResponse).containsKey("success").containsKey("timestamp");
        assertThat(unauthorizedResponse).containsKey("success").containsKey("timestamp");
        assertThat(notFoundResponse).containsKey("success").containsKey("timestamp");
        
        // Success response should have success=true
        assertThat(successResponse.get("success")).isEqualTo(true);
        
        // Error responses should have success=false
        assertThat(errorResponse.get("success")).isEqualTo(false);
        assertThat(unauthorizedResponse.get("success")).isEqualTo(false);
        assertThat(notFoundResponse.get("success")).isEqualTo(false);
    }

    @Test
    @DisplayName("Edge Cases - Empty And Null Strings")
    void edgeCases_EmptyAndNullStrings_HandleGracefully() {
        // Test empty strings
        Map<String, Object> emptyMessageResponse = ResponseUtils.success("");
        Map<String, Object> emptyErrorResponse = ResponseUtils.error("");
        
        assertThat(emptyMessageResponse.get("message")).isEqualTo("");
        assertThat(emptyErrorResponse.get("error")).isEqualTo("");
        
        // Test null strings (should not cause exceptions)
        Map<String, Object> nullMessageResponse = ResponseUtils.success(null);
        Map<String, Object> nullErrorResponse = ResponseUtils.error(null);
        
        assertThat(nullMessageResponse.get("message")).isNull();
        assertThat(nullErrorResponse.get("error")).isNull();
    }
}
