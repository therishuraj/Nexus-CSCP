package com.nexus.user_service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    /**
     * Create successful response with data
     * @param message success message
     * @param data response data
     * @param <T> data type
     * @return success response map
     */
    public static <T> Map<String, Object> success(String message, T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create successful response without data
     * @param message success message
     * @return success response map
     */
    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create error response
     * @param message error message
     * @return error response map
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create error response with error details
     * @param message error message
     * @param errorDetails additional error details
     * @return error response map
     */
    public static Map<String, Object> error(String message, Object errorDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("details", errorDetails);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create validation error response
     * @param validationErrors map of field validation errors
     * @return validation error response map
     */
    public static Map<String, Object> validationError(Map<String, String> validationErrors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Validation failed");
        response.put("validationErrors", validationErrors);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create unauthorized response
     * @return unauthorized response map
     */
    public static Map<String, Object> unauthorized() {
        return unauthorized("Unauthorized access");
    }
    
    /**
     * Create unauthorized response with custom message
     * @param message custom unauthorized message
     * @return unauthorized response map
     */
    public static Map<String, Object> unauthorized(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("code", "UNAUTHORIZED");
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create not found response
     * @param resource name of the resource not found
     * @return not found response map
     */
    public static Map<String, Object> notFound(String resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", resource + " not found");
        response.put("code", "NOT_FOUND");
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create conflict response (for duplicate resources)
     * @param message conflict message
     * @return conflict response map
     */
    public static Map<String, Object> conflict(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("code", "CONFLICT");
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create paginated response
     * @param data list of data
     * @param page current page number
     * @param size page size
     * @param total total number of items
     * @param <T> data type
     * @return paginated response map
     */
    public static <T> Map<String, Object> paginated(T data, int page, int size, long total) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", page);
        pagination.put("size", size);
        pagination.put("total", total);
        pagination.put("totalPages", (int) Math.ceil((double) total / size));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("pagination", pagination);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create login success response
     * @param token JWT token or session token
     * @param userId user ID
     * @return login response map
     */
    public static Map<String, Object> loginSuccess(String token, String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("userId", userId);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Create deletion success response
     * @param resource name of deleted resource
     * @return deletion response map
     */
    public static Map<String, Object> deleted(String resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", resource + " deleted successfully");
        response.put("deleted", true);
        response.put("timestamp", getCurrentTimestamp());
        return response;
    }
    
    /**
     * Get current timestamp as formatted string
     * @return formatted timestamp string
     */
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
}
