package com.nexus.user_service.service;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.request.UserUpdateRequestDTO;
import com.nexus.user_service.dto.request.UserValidationRequestDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.dto.response.UserBatchResponseDTO;
import com.nexus.user_service.model.User;
import com.nexus.user_service.repository.UserRepository;
import com.nexus.user_service.utils.LoggerUtils;
import com.nexus.user_service.utils.MapperUtils;
import com.nexus.user_service.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerUtils.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User createUser(UserCreateRequestDTO request) {
        logger.info("Starting user creation process - Email: {}, Roles: {}", request.getEmail(), request.getRoles());
        
        // Check if user already exists
        logger.debug("Checking if user exists with email: {}", request.getEmail());
        boolean userExists = userRepository.existsByEmail(request.getEmail());
        if (userExists) {
            logger.warn("User creation failed - User already exists with email: {}", request.getEmail());
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        logger.debug("Email availability confirmed - proceeding with user creation");
        
        // Use MapperUtils to convert DTO to User entity
        logger.debug("Converting DTO to User entity");
        User user = MapperUtils.toUser(request);
        
        // Hash password
        logger.debug("Generating password hash for user");
        user.setPasswordHash(PasswordUtils.hashPassword(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        logger.debug("Saving user to database - Email: {}, Roles: {}, Wallet Balance: {}", 
            user.getEmail(), user.getRoles(), user.getWalletBalance());
        User savedUser = userRepository.save(user);
        
        logger.info("User created successfully - ID: {}, Email: {}, Roles: {}, Wallet Balance: {}", 
            savedUser.getId(), savedUser.getEmail(), savedUser.getRoles(), 
            savedUser.getWalletBalance());
        
        return savedUser;
    }
    
    @Override
    public User authenticateUser(String email, String password) {
        logger.info("Authenticating user with email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                logger.info("Authentication successful for user: {}", email);
                return user;
            }
        }
        
        logger.warn("Authentication failed for user: {}", email);
        return null;
    }
    
    @Override
    public List<User> getAllUsers() {
        logger.info("Starting fetch all users operation");
        logger.debug("Executing database query to retrieve all users");
        
        List<User> users = userRepository.findAll();
        
        logger.info("Retrieved {} users from database", users.size());
        logger.debug("Users found with roles distribution - Total: {}", users.size());
        
        return users;
    }
    
    @Override
    public Optional<User> getUserById(String id) {
        logger.info("Starting fetch user by ID operation - ID: {}", id);
        logger.debug("Executing database query to find user by ID: {}", id);
        
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found by ID - ID: {}, Email: {}, Roles: {}, Wallet Balance: {}", 
                user.getId(), user.getEmail(), user.getRoles(), user.getWalletBalance());
        } else {
            logger.warn("No user found with ID: {}", id);
        }
        
        return userOpt;
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.info("Starting fetch user by email operation - Email: {}", email);
        logger.debug("Executing database query to find user by email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found by email - ID: {}, Email: {}, Roles: {}", 
                user.getId(), user.getEmail(), user.getRoles());
        } else {
            logger.warn("No user found with email: {}", email);
        }
        
        return userOpt;
    }
    
    @Override
    public User updateUser(String id, UserUpdateRequestDTO request) {
        logger.info("Starting user update process - ID: {}, Email: {}, Wallet Balance: {}, Wallet Adjustment: {}", 
            id, request.getEmail(), request.getWalletBalance(), request.getWalletAdjustment());
        
        logger.debug("Fetching user from database - ID: {}", id);
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            logger.warn("User update failed - User not found with ID: {}", id);
            throw new RuntimeException("User with ID " + id + " not found");
        }
        
        User user = userOpt.get();
        logger.debug("User found - ID: {}, Current Email: {}, Current Wallet Balance: {}, Current Funding Requests: {}", 
            user.getId(), user.getEmail(), user.getWalletBalance(), user.getFundingRequestIds().size());
        
        // Check if new email is already taken by another user
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            logger.debug("Checking email availability - New email: {}", request.getEmail());
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("User update failed - Email already taken: {}", request.getEmail());
                throw new RuntimeException("Email " + request.getEmail() + " is already taken");
            }
            logger.debug("Email availability confirmed - Email: {}", request.getEmail());
        }
        
        // Log wallet operations if present
        if (request.getWalletAdjustment() != null) {
            logger.info("Wallet adjustment operation - User ID: {}, Current Balance: {}, Adjustment: {}", 
                id, user.getWalletBalance(), request.getWalletAdjustment());
        }
        
        // Log funding request changes if present
        if (request.getFundingRequestIds() != null) {
            logger.info("Funding request update - User ID: {}, Current Funding Requests: {}, New Funding Requests: {}", 
                id, user.getFundingRequestIds().size(), request.getFundingRequestIds().size());
        }
        
        // Use MapperUtils to update user from DTO
        logger.debug("Updating user entity from DTO");
        MapperUtils.updateUserFromDTO(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        
        logger.debug("Saving updated user to database - ID: {}", id);
        User updatedUser = userRepository.save(user);
        
        logger.info("User updated successfully - ID: {}, Email: {}, Wallet Balance: {}, Funding Requests: {}", 
            updatedUser.getId(), updatedUser.getEmail(), updatedUser.getWalletBalance(), 
            updatedUser.getFundingRequestIds().size());
        
        return updatedUser;
    }
    
    @Override
    public boolean deleteUser(String id) {
        logger.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        
        userRepository.deleteById(id);
        logger.info("User deleted successfully: {}", id);
        
        return true;
    }
    
    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        logger.info("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }
    
    @Override
    public UserResponseDTO validateUser(UserValidationRequestDTO request) {
        logger.info("Validating user with email: {}", request.getEmail());
        
        // Validate input
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        
        // Use existing authenticate method
        User user = authenticateUser(request.getEmail(), request.getPassword());
        
        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Convert User to UserResponseDTO
        UserResponseDTO response = MapperUtils.toUserResponseDTO(user);
        
        logger.info("User validation successful for: {}", request.getEmail());
        return response;
    }
    
    @Override
    public List<UserBatchResponseDTO> getUsersBatch(List<String> userIds) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting batch user lookup - Requested IDs count: {}", userIds != null ? userIds.size() : 0);
        
        // Validate input
        if (userIds == null || userIds.isEmpty()) {
            logger.warn("Batch user lookup failed - No user IDs provided");
            return new ArrayList<>();
        }
        
        logger.debug("Executing batch database query for {} user IDs", userIds.size());
        
        // Use optimized findAllById query to get all users in one database call
        Iterable<User> foundUsers = userRepository.findAllById(userIds);
        
        // Create a map for quick lookup of found users by ID
        Map<String, User> userMap = new HashMap<>();
        int foundCount = 0;
        for (User user : foundUsers) {
            userMap.put(user.getId(), user);
            foundCount++;
        }
        
        logger.debug("Found {} users out of {} requested IDs", foundCount, userIds.size());
        
        // Build response list maintaining the order of requested IDs
        List<UserBatchResponseDTO> response = new ArrayList<>();
        for (String userId : userIds) {
            User user = userMap.get(userId);
            if (user != null) {
                // User found - create response with user data
                UserBatchResponseDTO userResponse = new UserBatchResponseDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getRoles()
                );
                response.add(userResponse);
                logger.debug("Added user to batch response - ID: {}, Email: {}, Roles: {}", 
                    user.getId(), user.getEmail(), user.getRoles());
            } else {
                // User not found - create response with null values
                UserBatchResponseDTO userResponse = new UserBatchResponseDTO(userId, null, null);
                response.add(userResponse);
                logger.debug("Added not-found entry to batch response - ID: {}", userId);
            }
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Batch user lookup completed - Requested: {}, Found: {}, Not Found: {}, Execution time: {}ms", 
            userIds.size(), foundCount, (userIds.size() - foundCount), executionTime);
        
        return response;
    }
}
