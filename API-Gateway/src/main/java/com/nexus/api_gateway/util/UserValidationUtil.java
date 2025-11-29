package com.nexus.api_gateway.util;

import com.nexus.api_gateway.dto.LoginRequest;
import java.util.List;
import java.util.Map;

public final class UserValidationUtil {

    private UserValidationUtil() {}

    public static boolean isValidLoginRequest(LoginRequest request) {
        return request != null &&
                request.getEmail() != null && !request.getEmail().isEmpty() &&
                request.getPassword() != null && !request.getPassword().isEmpty();
    }

    public static boolean isValidUserMap(Map<String, Object> userMap) {
        if (userMap == null) return false;
        String email = (String) userMap.get("email");
        List<String> roles = (List<String>) userMap.get("roles");
        return email != null && !email.isEmpty() && roles != null && !roles.isEmpty();
    }

    public static boolean isValidSignupRequest(Map<String, Object> request) {
        if (request == null) return false;
    
        Object email = request.get("email");
        Object password = request.get("password");
    
        if (email == null || password == null) return false;
    
        String emailStr = email.toString().trim();
        String passStr = password.toString().trim();
    
        return !emailStr.isEmpty() && !passStr.isEmpty();
    }


    /**
     * Extracts the email from the user data map.
     * @param userMap the map containing user data
     * @return the user's email, or null if not present
     */
    public static String extractEmail(Map<String, Object> userMap) {
        if (userMap == null) {
            return null;
        }
        return (String) userMap.get("email");
    }

    public static String extractId(Map<String, Object> userMap) {
        if (userMap == null) {
            return null;
        }
        return (String) userMap.get("id");
    }

    /**
     * Extracts the roles list from the user data map.
     * @param userMap the map containing user data
     * @return the list of roles, or null if not present
     */
    @SuppressWarnings("unchecked")
    public static List<String> extractRoles(Map<String, Object> userMap) {
        if (userMap == null) {
            return null;
        }
        return (List<String>) userMap.get("roles");
    }
}
