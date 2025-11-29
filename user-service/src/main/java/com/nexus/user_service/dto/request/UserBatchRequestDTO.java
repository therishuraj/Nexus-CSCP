package com.nexus.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Request DTO for batch user lookup operations")
public class UserBatchRequestDTO {
    
    @Schema(description = "List of user IDs to retrieve in batch", example = "[\"507f1f77bcf86cd799439011\", \"507f1f77bcf86cd799439012\"]", required = true)
    private List<String> userIds;
    
    // Default constructor
    public UserBatchRequestDTO() {}
    
    // Constructor with userIds
    public UserBatchRequestDTO(List<String> userIds) {
        this.userIds = userIds;
    }
    
    // Getters and Setters
    public List<String> getUserIds() {
        return userIds;
    }
    
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
    
    @Override
    public String toString() {
        return "UserBatchRequestDTO{" +
                "userIds=" + userIds +
                '}';
    }
}
