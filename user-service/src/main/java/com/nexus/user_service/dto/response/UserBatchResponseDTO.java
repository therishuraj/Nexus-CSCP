package com.nexus.user_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response DTO for batch user lookup operations containing user details or null for not found users")
public class UserBatchResponseDTO {
    
    @Schema(description = "User ID", example = "507f1f77bcf86cd799439011")
    private String id;
    
    @Schema(description = "User email address (null if user not found)", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User roles (null if user not found)", example = "[\"USER\", \"ADMIN\"]")
    private List<String> roles;

    // Default constructor
    public UserBatchResponseDTO() {}

    // Constructor with all fields
    public UserBatchResponseDTO(String id, String email, List<String> roles) {
        this.id = id;
        this.email = email;
        this.roles = roles;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserBatchResponseDTO{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
