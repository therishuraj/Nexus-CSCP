package com.nexus.user_service.controller;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.request.UserUpdateRequestDTO;
import com.nexus.user_service.dto.request.UserValidationRequestDTO;
import com.nexus.user_service.dto.request.UserBatchRequestDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.dto.response.UserListResponseDTO;
import com.nexus.user_service.dto.response.UserBatchResponseDTO;
import com.nexus.user_service.model.User;
import com.nexus.user_service.service.UserService;
import com.nexus.user_service.utils.LoggerUtils;
import com.nexus.user_service.utils.ValidationUtils;
import com.nexus.user_service.utils.ResponseUtils;
import com.nexus.user_service.utils.MapperUtils;
import com.nexus.user_service.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerUtils.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new user
     * POST /api/v1/users
     * Request: UserCreateRequestDTO
     * Response: UserResponseDTO
     */
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserCreateRequestDTO request) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("User creation request received - Email: {}, Roles: {}, Initial wallet balance: {}", 
                request.getEmail(), request.getRoles(), request.getWalletBalance());
            
            // Validate request
            logger.debug("Starting validation for user creation request");
            String validationError = ValidationUtils.validateUserCreateRequest(request);
            if (validationError != null) {
                logger.warn("User creation validation failed - Email: {}, Error: {}", request.getEmail(), validationError);
                return ResponseEntity.badRequest().body(ResponseUtils.error(validationError));
            }
            
            // Validate email format
            if (!ValidationUtils.isValidEmail(request.getEmail())) {
                logger.warn("Invalid email format provided - Email: {}", request.getEmail());
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid email format"));
            }
            
            logger.debug("Validation completed successfully for user creation");
            User user = userService.createUser(request);
            
            UserResponseDTO response = MapperUtils.toUserResponseDTO(user);
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("User created successfully - ID: {}, Email: {}, Roles: {}, Wallet Balance: {}, Execution time: {}ms", 
                user.getId(), user.getEmail(), user.getRoles(), user.getWalletBalance(), executionTime);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtils.success("User created successfully", response));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("User creation failed - Email: {}, Error: {}, Execution time: {}ms", 
                request.getEmail(), e.getMessage(), executionTime, e);
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage()));
        }
    }
    
    /**
     * Validate user credentials
     * POST /api/v1/auth/validate-user
     * Request: UserValidationRequestDTO
     * Response: UserResponseDTO or 401 Unauthorized
     */
    @PostMapping("/auth/user/validate")
    public ResponseEntity<Map<String, Object>> validateUser(@RequestBody UserValidationRequestDTO request) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("User validation request received - Email: {}", request.getEmail());
            
            // Validate request
            logger.debug("Starting validation for user validation request");
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                logger.warn("User validation failed - Email is required");
                return ResponseEntity.badRequest().body(ResponseUtils.error("Email is required"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.warn("User validation failed - Password is required");
                return ResponseEntity.badRequest().body(ResponseUtils.error("Password is required"));
            }
            
            // Validate email format
            if (!ValidationUtils.isValidEmail(request.getEmail())) {
                logger.warn("User validation failed - Invalid email format: {}", request.getEmail());
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid email format"));
            }
            
            logger.debug("Input validation completed, proceeding with user credential verification");
            UserResponseDTO response = userService.validateUser(request);
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("User validation successful - Email: {}, User ID: {}, Execution time: {}ms", 
                request.getEmail(), response.getId(), executionTime);
            
            return ResponseEntity.ok(ResponseUtils.success("User validation successful", response));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.warn("User validation failed - Email: {}, Error: {}, Execution time: {}ms", 
                request.getEmail(), e.getMessage(), executionTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.unauthorized("Invalid credentials"));
        }
    }
    
    /**
     * Get all users
     * GET /api/v1/users
     * Response: List of UserListResponseDTO
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Get all users request received");
            
            List<User> users = userService.getAllUsers();
            List<UserListResponseDTO> response = MapperUtils.toUserListResponseDTOs(users);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Retrieved {} users successfully - Execution time: {}ms", users.size(), executionTime);
            return ResponseEntity.ok(ResponseUtils.success("Users retrieved successfully", response));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Failed to retrieve users - Error: {}, Execution time: {}ms", e.getMessage(), executionTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error(e.getMessage()));
        }
    }
    
    /**
     * Get user by ID
     * GET /api/v1/users/{id}
     * Response: UserResponseDTO
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Get user by ID request received - ID: {}", id);
            
            // Validate ID
            if (!ValidationUtils.isValidId(id)) {
                logger.warn("Invalid user ID format provided: {}", id);
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid user ID format"));
            }
            
            logger.debug("ID validation completed, fetching user from database");
            Optional<User> userOpt = userService.getUserById(id);
            
            if (userOpt.isPresent()) {
                UserResponseDTO response = MapperUtils.toUserDetailResponseDTO(userOpt.get());
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("User retrieved successfully - ID: {}, Email: {}, Wallet Balance: {}, Execution time: {}ms", 
                    userOpt.get().getId(), userOpt.get().getEmail(), userOpt.get().getWalletBalance(), executionTime);
                return ResponseEntity.ok(ResponseUtils.success("User retrieved successfully", response));
            } else {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.warn("User not found - ID: {}, Execution time: {}ms", id, executionTime);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtils.notFound("User"));
            }
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Failed to retrieve user by ID - ID: {}, Error: {}, Execution time: {}ms", 
                id, e.getMessage(), executionTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error(e.getMessage()));
        }
    }
    
    /**
     * Update user
     * PUT /api/v1/users/{id}
     * Request: UserUpdateRequestDTO
     * Response: UserResponseDTO
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String id, 
            @RequestBody UserUpdateRequestDTO request) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("User update request received - ID: {}, Email: {}, Wallet Balance: {}, Wallet Adjustment: {}, Funding Request IDs: {}", 
                id, request.getEmail(), request.getWalletBalance(), request.getWalletAdjustment(), 
                request.getFundingRequestIds() != null ? request.getFundingRequestIds().size() : 0);
            
            // Validate ID
            if (!ValidationUtils.isValidId(id)) {
                logger.warn("Invalid user ID format provided: {}", id);
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid user ID format"));
            }
            
            // Validate request
            logger.debug("Starting validation for user update request");
            String validationError = ValidationUtils.validateUserUpdateRequest(request);
            if (validationError != null) {
                logger.warn("User update validation failed - ID: {}, Error: {}", id, validationError);
                return ResponseEntity.badRequest().body(ResponseUtils.error(validationError));
            }
            
            // Validate email format if provided
            if (request.getEmail() != null && !ValidationUtils.isValidEmail(request.getEmail())) {
                logger.warn("Invalid email format in update request - ID: {}, Email: {}", id, request.getEmail());
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid email format"));
            }
            
            // Log wallet adjustment details if present
            if (request.getWalletAdjustment() != null) {
                logger.info("Wallet adjustment requested - User ID: {}, Adjustment amount: {}", 
                    id, request.getWalletAdjustment());
            }
            
            logger.debug("Validation completed, proceeding with user update");
            User updatedUser = userService.updateUser(id, request);
            UserResponseDTO response = MapperUtils.toUserResponseDTO(updatedUser);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("User updated successfully - ID: {}, Email: {}, Wallet Balance: {}, Funding Requests: {}, Execution time: {}ms", 
                updatedUser.getId(), updatedUser.getEmail(), updatedUser.getWalletBalance(), 
                updatedUser.getFundingRequestIds().size(), executionTime);
            
            return ResponseEntity.ok(ResponseUtils.success("User updated successfully", response));
            
        } catch (ExceptionUtils.InsufficientFundsException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.warn("Wallet operation failed due to insufficient funds - User ID: {}, Error: {}, Execution time: {}ms", 
                id, e.getMessage(), executionTime);
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage()));
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("User update failed - ID: {}, Error: {}, Execution time: {}ms", 
                id, e.getMessage(), executionTime, e);
            
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtils.notFound("User"));
            } else {
                return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage()));
            }
        }
    }
    
    /**
     * Delete user
     * DELETE /api/v1/users/{id}
     * Response: { message: "User deleted successfully" }
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Delete user request received - ID: {}", id);
            
            // Validate ID
            if (!ValidationUtils.isValidId(id)) {
                logger.warn("Invalid user ID format provided for deletion: {}", id);
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid user ID format"));
            }
            
            logger.debug("ID validation completed, proceeding with user deletion");
            boolean deleted = userService.deleteUser(id);
            
            if (deleted) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("User deleted successfully - ID: {}, Execution time: {}ms", id, executionTime);
                return ResponseEntity.ok(ResponseUtils.deleted("User"));
            } else {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.error("Failed to delete user - ID: {}, Execution time: {}ms", id, executionTime);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error("Failed to delete user"));
            }
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("User deletion failed - ID: {}, Error: {}, Execution time: {}ms", 
                id, e.getMessage(), executionTime, e);
            
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtils.notFound("User"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error(e.getMessage()));
            }
        }
    }
    
    /**
     * Get multiple users by their IDs in batch
     * POST /api/v1/users/batch
     * Request: UserBatchRequestDTO
     * Response: Array of UserBatchResponseDTO
     */
    @PostMapping("/users/batch")
    public ResponseEntity<Map<String, Object>> getUsersBatch(@RequestBody UserBatchRequestDTO request) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Batch user lookup request received - User IDs count: {}", 
                request.getUserIds() != null ? request.getUserIds().size() : 0);
            
            // Validate request
            if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
                logger.warn("Batch user lookup failed - No user IDs provided");
                return ResponseEntity.badRequest().body(ResponseUtils.error("User IDs are required"));
            }
            
            // Validate each user ID format
            for (String userId : request.getUserIds()) {
                if (!ValidationUtils.isValidId(userId)) {
                    logger.warn("Invalid user ID format in batch request: {}", userId);
                    return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid user ID format: " + userId));
                }
            }
            
            logger.debug("Input validation completed, proceeding with batch user lookup");
            List<UserBatchResponseDTO> response = userService.getUsersBatch(request.getUserIds());
            
            long executionTime = System.currentTimeMillis() - startTime;
            long foundCount = response.stream().filter(user -> user.getEmail() != null).count();
            long notFoundCount = response.size() - foundCount;
            
            logger.info("Batch user lookup completed - Requested: {}, Found: {}, Not Found: {}, Execution time: {}ms", 
                request.getUserIds().size(), foundCount, notFoundCount, executionTime);
            
            return ResponseEntity.ok(ResponseUtils.success("Batch user lookup completed", response));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Batch user lookup failed - Error: {}, Execution time: {}ms", 
                e.getMessage(), executionTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error(e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/v1/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("Health check requested");
        Map<String, Object> healthData = Map.of(
            "status", "UP",
            "service", "user-service",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(ResponseUtils.success("Service is healthy", healthData));
    }
}
