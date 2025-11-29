package com.nexus.user_service.controller;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.model.User;
import com.nexus.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;
    private UserCreateRequestDTO createRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        // Create sample user
        sampleUser = new User();
        sampleUser.setId("507f1f77bcf86cd799439011");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setName("John Doe");
        sampleUser.setPasswordHash("$2a$10$hashedPassword");
        sampleUser.setRoles(Arrays.asList("FUNDER"));
        sampleUser.setWalletBalance(BigDecimal.valueOf(100.0));
        sampleUser.setFundingRequestIds(new ArrayList<>());
        sampleUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        sampleUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

        // Create sample DTO
        createRequestDTO = new UserCreateRequestDTO();
        createRequestDTO.setEmail("jane.doe@example.com");
        createRequestDTO.setName("Jane Doe");
        createRequestDTO.setPassword("password123");
        createRequestDTO.setRoles(Arrays.asList("FUNDER"));
        createRequestDTO.setWalletBalance(BigDecimal.valueOf(50.0));

        // Create response DTO
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(sampleUser.getId());
        userResponseDTO.setEmail(sampleUser.getEmail());
        userResponseDTO.setName(sampleUser.getName());
        userResponseDTO.setRoles(sampleUser.getRoles());
        userResponseDTO.setWalletBalance(sampleUser.getWalletBalance());
    }

    @Test
    @DisplayName("Create User - Success")
    void createUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateRequestDTO.class))).thenReturn(sampleUser);

        // When & Then
        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.email").value(sampleUser.getEmail()));
    }

    @Test
    @DisplayName("Get All Users - Success")
    void getAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(sampleUser);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Get User by ID - Success")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", sampleUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(sampleUser.getEmail()));
    }

    @Test
    @DisplayName("Get User by ID - Not Found")
    void getUserById_NotFound() throws Exception {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439999";
        when(userService.getUserById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Health Check - Success")
    void healthCheck_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Service is healthy"))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }
}
