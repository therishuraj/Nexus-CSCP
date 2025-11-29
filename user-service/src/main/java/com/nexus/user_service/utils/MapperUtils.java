package com.nexus.user_service.utils;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.request.UserUpdateRequestDTO;
import com.nexus.user_service.dto.response.UserListResponseDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MapperUtils {
    
    /**
     * Convert UserCreateRequestDTO to User entity
     * @param dto UserCreateRequestDTO
     * @return User entity
     */
    public static User toUser(UserCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setName(ValidationUtils.sanitizeInput(dto.getName()));
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setRoles(dto.getRoles());
        // Set wallet balance from DTO or default to 0
        user.setWalletBalance(dto.getWalletBalance() != null ? dto.getWalletBalance() : BigDecimal.ZERO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        // Note: Password will be hashed in service layer
        
        return user;
    }
    
    /**
     * Convert User entity to UserResponseDTO
     * @param user User entity
     * @return UserResponseDTO
     */
    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setWalletBalance(user.getWalletBalance());
        dto.setFundingRequestIds(user.getFundingRequestIds());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        // Note: passwordHash is NOT included for security
        
        return dto;
    }
    
    /**
     * Convert User entity to UserListResponseDTO (for list endpoints)
     * @param user User entity
     * @return UserListResponseDTO
     */
    public static UserListResponseDTO toUserListResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserListResponseDTO dto = new UserListResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setRoles(user.getRoles());
        dto.setWalletBalance(user.getWalletBalance());
        
        return dto;
    }
    
    /**
     * Convert list of Users to list of UserListResponseDTO
     * @param users List of User entities
     * @return List of UserListResponseDTO
     */
    public static List<UserListResponseDTO> toUserListResponseDTOs(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                   .map(MapperUtils::toUserListResponseDTO)
                   .collect(Collectors.toList());
    }
    
    /**
     * Update User entity with UserUpdateRequestDTO data
     * @param user existing User entity
     * @param dto UserUpdateRequestDTO with update data
     */
    public static void updateUserFromDTO(User user, UserUpdateRequestDTO dto) {
        if (user == null || dto == null) {
            return;
        }
        
        if (ValidationUtils.isNotNullAndNotEmpty(dto.getName())) {
            user.setName(ValidationUtils.sanitizeInput(dto.getName()));
        }
        
        if (ValidationUtils.isNotNullAndNotEmpty(dto.getEmail())) {
            user.setEmail(dto.getEmail().toLowerCase().trim());
        }
        
        if (dto.getWalletBalance() != null) {
            user.setWalletBalance(dto.getWalletBalance());
        }
        
        // Handle wallet adjustment (add/deduct from current balance)
        if (dto.getWalletAdjustment() != null) {
            BigDecimal adjustment = dto.getWalletAdjustment();
            BigDecimal currentBalance = user.getWalletBalance();
            BigDecimal newBalance = currentBalance.add(adjustment);
            
            // Check for insufficient funds on deduction (negative adjustment)
            if (adjustment.compareTo(BigDecimal.ZERO) < 0 && 
                newBalance.compareTo(BigDecimal.ZERO) < 0) {
                
                throw new ExceptionUtils.InsufficientFundsException(currentBalance, adjustment);
            }
            
            user.setWalletBalance(newBalance);
        }
        
        if (dto.getFundingRequestIds() != null) {
            List<String> existingIds = user.getFundingRequestIds();
            
            // Append each new ID if it doesn't already exist (avoid duplicates)
            for (String newId : dto.getFundingRequestIds()) {
                if (!existingIds.contains(newId)) {
                    existingIds.add(newId);
                }
            }
        }
        
        user.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Create a simple User creation response DTO (id, name, roles only)
     * @param user User entity
     * @return UserListResponseDTO with creation response format
     */
    public static UserListResponseDTO toUserCreationResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserListResponseDTO dto = new UserListResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setRoles(user.getRoles());
        dto.setWalletBalance(user.getWalletBalance());
        
        return dto;
    }
    
    /**
     * Create a detailed User response DTO (for GET by ID endpoint)
     * @param user User entity
     * @return UserResponseDTO with id, name, email only
     */
    public static UserResponseDTO toUserDetailResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setWalletBalance(user.getWalletBalance());
        dto.setFundingRequestIds(user.getFundingRequestIds());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        // Note: Now includes all fields in GET by ID response
        
        return dto;
    }
}
