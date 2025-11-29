package com.nexus.user_service.service;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.request.UserUpdateRequestDTO;
import com.nexus.user_service.dto.request.UserValidationRequestDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.dto.response.UserBatchResponseDTO;
import com.nexus.user_service.model.User;
import com.nexus.user_service.repository.UserRepository;
import com.nexus.user_service.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserCreateRequestDTO createRequestDTO;
    private UserUpdateRequestDTO updateRequestDTO;
    private UserValidationRequestDTO validationRequestDTO;

    @BeforeEach
    void setUp() {
        // Create sample user
        sampleUser = new User();
        sampleUser.setId("507f1f77bcf86cd799439011");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setName("John Doe");
        sampleUser.setPasswordHash("$2a$10$hashedPassword");
        sampleUser.setRoles(Arrays.asList("USER"));
        sampleUser.setWalletBalance(BigDecimal.valueOf(100.0));
        sampleUser.setFundingRequestIds(new ArrayList<>());
        sampleUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        sampleUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

        // Create sample DTOs
        createRequestDTO = new UserCreateRequestDTO();
        createRequestDTO.setEmail("jane.doe@example.com");
        createRequestDTO.setName("Jane Doe");
        createRequestDTO.setPassword("password123");
        createRequestDTO.setRoles(Arrays.asList("USER"));
        createRequestDTO.setWalletBalance(BigDecimal.valueOf(50.0));

        updateRequestDTO = new UserUpdateRequestDTO();
        updateRequestDTO.setEmail("john.updated@example.com");
        updateRequestDTO.setName("John Updated");

        validationRequestDTO = new UserValidationRequestDTO();
        validationRequestDTO.setEmail("john.doe@example.com");
        validationRequestDTO.setPassword("password123");
    }

    @Test
    @DisplayName("Create User - Success")
    void createUser_Success() {
        // Given
        when(userRepository.existsByEmail(createRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        try (MockedStatic<PasswordUtils> passwordUtilsMock = Mockito.mockStatic(PasswordUtils.class)) {
            passwordUtilsMock.when(() -> PasswordUtils.hashPassword(createRequestDTO.getPassword()))
                    .thenReturn("$2a$10$hashedPassword");

            // When
            User result = userService.createUser(createRequestDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(sampleUser.getId());
            assertThat(result.getEmail()).isEqualTo(sampleUser.getEmail());
            verify(userRepository).existsByEmail(createRequestDTO.getEmail());
            verify(userRepository).save(any(User.class));
            passwordUtilsMock.verify(() -> PasswordUtils.hashPassword(createRequestDTO.getPassword()));
        }
    }

    @Test
    @DisplayName("Create User - User Already Exists")
    void createUser_UserAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(createRequestDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email " + createRequestDTO.getEmail() + " already exists");

        verify(userRepository).existsByEmail(createRequestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Authenticate User - Success")
    void authenticateUser_Success() {
        // Given
        when(userRepository.findByEmail(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        try (MockedStatic<PasswordUtils> passwordUtilsMock = Mockito.mockStatic(PasswordUtils.class)) {
            passwordUtilsMock.when(() -> PasswordUtils.verifyPassword("password123", sampleUser.getPasswordHash()))
                    .thenReturn(true);

            // When
            User result = userService.authenticateUser(sampleUser.getEmail(), "password123");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(sampleUser.getEmail());
            verify(userRepository).findByEmail(sampleUser.getEmail());
            passwordUtilsMock.verify(() -> PasswordUtils.verifyPassword("password123", sampleUser.getPasswordHash()));
        }
    }

    @Test
    @DisplayName("Authenticate User - Invalid Password")
    void authenticateUser_InvalidPassword() {
        // Given
        when(userRepository.findByEmail(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        try (MockedStatic<PasswordUtils> passwordUtilsMock = Mockito.mockStatic(PasswordUtils.class)) {
            passwordUtilsMock.when(() -> PasswordUtils.verifyPassword("wrongpassword", sampleUser.getPasswordHash()))
                    .thenReturn(false);

            // When
            User result = userService.authenticateUser(sampleUser.getEmail(), "wrongpassword");

            // Then
            assertThat(result).isNull();
            verify(userRepository).findByEmail(sampleUser.getEmail());
            passwordUtilsMock.verify(() -> PasswordUtils.verifyPassword("wrongpassword", sampleUser.getPasswordHash()));
        }
    }

    @Test
    @DisplayName("Authenticate User - User Not Found")
    void authenticateUser_UserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        User result = userService.authenticateUser("nonexistent@example.com", "password123");

        // Then
        assertThat(result).isNull();
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Get All Users - Success")
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(sampleUser, createSecondUser());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo(sampleUser.getEmail());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get All Users - Empty List")
    void getAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get User By ID - Success")
    void getUserById_Success() {
        // Given
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        // When
        Optional<User> result = userService.getUserById(sampleUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(sampleUser.getId());
        verify(userRepository).findById(sampleUser.getId());
    }

    @Test
    @DisplayName("Get User By ID - Not Found")
    void getUserById_NotFound() {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439999";
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Get User By Email - Success")
    void getUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        // When
        Optional<User> result = userService.getUserByEmail(sampleUser.getEmail());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(sampleUser.getEmail());
        verify(userRepository).findByEmail(sampleUser.getEmail());
    }

    @Test
    @DisplayName("Get User By Email - Not Found")
    void getUserByEmail_NotFound() {
        // Given
        String nonExistentEmail = "notfound@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail(nonExistentEmail);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Update User - Success")
    void updateUser_Success() {
        // Given
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail(updateRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // When
        User result = userService.updateUser(sampleUser.getId(), updateRequestDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(sampleUser.getId());
        verify(userRepository).existsByEmail(updateRequestDTO.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - User Not Found")
    void updateUser_UserNotFound() {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439999";
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(nonExistentId, updateRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with ID " + nonExistentId + " not found");

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Email Already Taken")
    void updateUser_EmailAlreadyTaken() {
        // Given
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail(updateRequestDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(sampleUser.getId(), updateRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email " + updateRequestDTO.getEmail() + " is already taken");

        verify(userRepository).findById(sampleUser.getId());
        verify(userRepository).existsByEmail(updateRequestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Delete User - Success")
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(sampleUser.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(sampleUser.getId());

        // When
        boolean result = userService.deleteUser(sampleUser.getId());

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsById(sampleUser.getId());
        verify(userRepository).deleteById(sampleUser.getId());
    }

    @Test
    @DisplayName("Delete User - User Not Found")
    void deleteUser_UserNotFound() {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439999";
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with ID " + nonExistentId + " not found");

        verify(userRepository).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("User Exists By Email - True")
    void userExistsByEmail_True() {
        // Given
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

        // When
        boolean result = userService.userExistsByEmail(sampleUser.getEmail());

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(sampleUser.getEmail());
    }

    @Test
    @DisplayName("User Exists By Email - False")
    void userExistsByEmail_False() {
        // Given
        String nonExistentEmail = "notfound@example.com";
        when(userRepository.existsByEmail(nonExistentEmail)).thenReturn(false);

        // When
        boolean result = userService.userExistsByEmail(nonExistentEmail);

        // Then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Get Users By Role - Success")
    void getUsersByRole_Success() {
        // Given
        List<User> users = Arrays.asList(sampleUser);
        when(userRepository.findByRole("USER")).thenReturn(users);

        // When
        List<User> result = userService.getUsersByRole("USER");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoles()).contains("USER");
        verify(userRepository).findByRole("USER");
    }

    @Test
    @DisplayName("Validate User - Success")
    void validateUser_Success() {
        // Given
        when(userRepository.findByEmail(validationRequestDTO.getEmail())).thenReturn(Optional.of(sampleUser));

        try (MockedStatic<PasswordUtils> passwordUtilsMock = Mockito.mockStatic(PasswordUtils.class)) {
            passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(validationRequestDTO.getPassword(), sampleUser.getPasswordHash()))
                    .thenReturn(true);

            // When
            UserResponseDTO result = userService.validateUser(validationRequestDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(sampleUser.getEmail());
            verify(userRepository).findByEmail(validationRequestDTO.getEmail());
        }
    }

    @Test
    @DisplayName("Validate User - Missing Email")
    void validateUser_MissingEmail() {
        // Given
        validationRequestDTO.setEmail(null);

        // When & Then
        assertThatThrownBy(() -> userService.validateUser(validationRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is required");
    }

    @Test
    @DisplayName("Validate User - Missing Password")
    void validateUser_MissingPassword() {
        // Given
        validationRequestDTO.setPassword(null);

        // When & Then
        assertThatThrownBy(() -> userService.validateUser(validationRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Password is required");
    }

    @Test
    @DisplayName("Validate User - Invalid Credentials")
    void validateUser_InvalidCredentials() {
        // Given
        when(userRepository.findByEmail(validationRequestDTO.getEmail())).thenReturn(Optional.of(sampleUser));

        try (MockedStatic<PasswordUtils> passwordUtilsMock = Mockito.mockStatic(PasswordUtils.class)) {
            passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(validationRequestDTO.getPassword(), sampleUser.getPasswordHash()))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> userService.validateUser(validationRequestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Invalid credentials");
        }
    }

    @Test
    @DisplayName("Get Users Batch - Success")
    void getUsersBatch_Success() {
        // Given
        List<String> userIds = Arrays.asList(sampleUser.getId(), "507f1f77bcf86cd799439999");
        List<User> foundUsers = Arrays.asList(sampleUser);
        when(userRepository.findAllById(userIds)).thenReturn(foundUsers);

        // When
        List<UserBatchResponseDTO> result = userService.getUsersBatch(userIds);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(sampleUser.getId());
        assertThat(result.get(0).getEmail()).isEqualTo(sampleUser.getEmail());
        assertThat(result.get(1).getId()).isEqualTo("507f1f77bcf86cd799439999");
        assertThat(result.get(1).getEmail()).isNull(); // Not found user
        verify(userRepository).findAllById(userIds);
    }

    @Test
    @DisplayName("Get Users Batch - Empty Input")
    void getUsersBatch_EmptyInput() {
        // Given
        List<String> emptyList = new ArrayList<>();

        // When
        List<UserBatchResponseDTO> result = userService.getUsersBatch(emptyList);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository, never()).findAllById(anyList());
    }

    @Test
    @DisplayName("Get Users Batch - Null Input")
    void getUsersBatch_NullInput() {
        // When
        List<UserBatchResponseDTO> result = userService.getUsersBatch(null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository, never()).findAllById(anyList());
    }

    // Helper method to create a second user for testing
    private User createSecondUser() {
        User secondUser = new User();
        secondUser.setId("507f1f77bcf86cd799439012");
        secondUser.setEmail("jane.doe@example.com");
        secondUser.setName("Jane Doe");
        secondUser.setPasswordHash("$2a$10$hashedPassword2");
        secondUser.setRoles(Arrays.asList("USER"));
        secondUser.setWalletBalance(BigDecimal.valueOf(200.0));
        secondUser.setFundingRequestIds(new ArrayList<>());
        secondUser.setCreatedAt(LocalDateTime.now().minusDays(2));
        secondUser.setUpdatedAt(LocalDateTime.now().minusDays(2));
        return secondUser;
    }
}
