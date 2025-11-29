package com.nexus.user_service.utils;

import com.nexus.user_service.dto.request.UserCreateRequestDTO;
import com.nexus.user_service.dto.request.UserUpdateRequestDTO;
import com.nexus.user_service.dto.response.UserListResponseDTO;
import com.nexus.user_service.dto.response.UserResponseDTO;
import com.nexus.user_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("MapperUtils Unit Tests")
class MapperUtilsTest {

    private UserCreateRequestDTO createRequestDTO;
    private UserUpdateRequestDTO updateRequestDTO;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        // Setup UserCreateRequestDTO
        createRequestDTO = new UserCreateRequestDTO();
        createRequestDTO.setName("John Doe");
        createRequestDTO.setEmail("John.Doe@Example.Com");
        createRequestDTO.setRoles(Arrays.asList("FUNDER"));
        createRequestDTO.setWalletBalance(BigDecimal.valueOf(100.50));

        // Setup UserUpdateRequestDTO
        updateRequestDTO = new UserUpdateRequestDTO();
        updateRequestDTO.setName("Updated Name");
        updateRequestDTO.setEmail("Updated.Email@Example.Com");
        updateRequestDTO.setWalletBalance(BigDecimal.valueOf(200.00));

        // Setup sample User
        sampleUser = new User();
        sampleUser.setId("507f1f77bcf86cd799439011");
        sampleUser.setName("Jane Smith");
        sampleUser.setEmail("jane.smith@example.com");
        sampleUser.setPasswordHash("$2a$10$hashedPassword");
        sampleUser.setRoles(Arrays.asList("INVESTOR", "FUNDER"));
        sampleUser.setWalletBalance(BigDecimal.valueOf(500.75));
        sampleUser.setFundingRequestIds(new ArrayList<>(Arrays.asList("req1", "req2")));
        sampleUser.setCreatedAt(LocalDateTime.now().minusDays(5));
        sampleUser.setUpdatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("ToUser - Valid DTO")
    void toUser_ValidDTO_ReturnsUserEntity() {
        // When
        User user = MapperUtils.toUser(createRequestDTO);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com"); // Should be lowercase
        assertThat(user.getRoles()).isEqualTo(Arrays.asList("FUNDER"));
        assertThat(user.getWalletBalance()).isEqualByComparingTo(BigDecimal.valueOf(100.50));
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getId()).isNull(); // Not set in mapper
        assertThat(user.getPasswordHash()).isNull(); // Not set in mapper
    }

    @Test
    @DisplayName("ToUser - Null DTO Returns Null")
    void toUser_NullDTO_ReturnsNull() {
        // When
        User user = MapperUtils.toUser(null);

        // Then
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("ToUser - Null Wallet Balance Uses Zero")
    void toUser_NullWalletBalance_UsesZero() {
        // Given
        createRequestDTO.setWalletBalance(null);

        // When
        User user = MapperUtils.toUser(createRequestDTO);

        // Then
        assertThat(user.getWalletBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("ToUser - Email Trimmed And Lowercased")
    void toUser_EmailTrimmingAndLowercasing() {
        // Given
        createRequestDTO.setEmail("  UPPERCASE.EMAIL@DOMAIN.COM  ");

        // When
        User user = MapperUtils.toUser(createRequestDTO);

        // Then
        assertThat(user.getEmail()).isEqualTo("uppercase.email@domain.com");
    }

    @Test
    @DisplayName("ToUserResponseDTO - Valid User")
    void toUserResponseDTO_ValidUser_ReturnsDTO() {
        // When
        UserResponseDTO dto = MapperUtils.toUserResponseDTO(sampleUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(sampleUser.getId());
        assertThat(dto.getName()).isEqualTo(sampleUser.getName());
        assertThat(dto.getEmail()).isEqualTo(sampleUser.getEmail());
        assertThat(dto.getRoles()).isEqualTo(sampleUser.getRoles());
        assertThat(dto.getWalletBalance()).isEqualByComparingTo(sampleUser.getWalletBalance());
        assertThat(dto.getFundingRequestIds()).isEqualTo(sampleUser.getFundingRequestIds());
        assertThat(dto.getCreatedAt()).isEqualTo(sampleUser.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(sampleUser.getUpdatedAt());
    }

    @Test
    @DisplayName("ToUserResponseDTO - Null User Returns Null")
    void toUserResponseDTO_NullUser_ReturnsNull() {
        // When
        UserResponseDTO dto = MapperUtils.toUserResponseDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("ToUserListResponseDTO - Valid User")
    void toUserListResponseDTO_ValidUser_ReturnsDTO() {
        // When
        UserListResponseDTO dto = MapperUtils.toUserListResponseDTO(sampleUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(sampleUser.getId());
        assertThat(dto.getName()).isEqualTo(sampleUser.getName());
        assertThat(dto.getRoles()).isEqualTo(sampleUser.getRoles());
        assertThat(dto.getWalletBalance()).isEqualByComparingTo(sampleUser.getWalletBalance());
    }

    @Test
    @DisplayName("ToUserListResponseDTO - Null User Returns Null")
    void toUserListResponseDTO_NullUser_ReturnsNull() {
        // When
        UserListResponseDTO dto = MapperUtils.toUserListResponseDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("ToUserListResponseDTOs - Valid Users List")
    void toUserListResponseDTOs_ValidUsersList_ReturnsDTOList() {
        // Given
        User user2 = new User();
        user2.setId("507f1f77bcf86cd799439022");
        user2.setName("Bob Johnson");
        user2.setRoles(Arrays.asList("SUPPLIER"));
        user2.setWalletBalance(BigDecimal.valueOf(300.00));
        
        List<User> users = Arrays.asList(sampleUser, user2);

        // When
        List<UserListResponseDTO> dtos = MapperUtils.toUserListResponseDTOs(users);

        // Then
        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
        
        assertThat(dtos.get(0).getId()).isEqualTo(sampleUser.getId());
        assertThat(dtos.get(0).getName()).isEqualTo(sampleUser.getName());
        
        assertThat(dtos.get(1).getId()).isEqualTo(user2.getId());
        assertThat(dtos.get(1).getName()).isEqualTo(user2.getName());
    }

    @Test
    @DisplayName("ToUserListResponseDTOs - Null List Returns Null")
    void toUserListResponseDTOs_NullList_ReturnsNull() {
        // When
        List<UserListResponseDTO> dtos = MapperUtils.toUserListResponseDTOs(null);

        // Then
        assertThat(dtos).isNull();
    }

    @Test
    @DisplayName("ToUserListResponseDTOs - Empty List Returns Empty List")
    void toUserListResponseDTOs_EmptyList_ReturnsEmptyList() {
        // When
        List<UserListResponseDTO> dtos = MapperUtils.toUserListResponseDTOs(new ArrayList<>());

        // Then
        assertThat(dtos).isNotNull();
        assertThat(dtos).isEmpty();
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Valid Inputs")
    void updateUserFromDTO_ValidInputs_UpdatesUser() {
        // Given
        LocalDateTime originalCreatedAt = sampleUser.getCreatedAt();
        LocalDateTime originalUpdatedAt = sampleUser.getUpdatedAt();

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        assertThat(sampleUser.getName()).isEqualTo("Updated Name");
        assertThat(sampleUser.getEmail()).isEqualTo("updated.email@example.com"); // Lowercased
        assertThat(sampleUser.getWalletBalance()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(sampleUser.getCreatedAt()).isEqualTo(originalCreatedAt); // Should not change
        assertThat(sampleUser.getUpdatedAt()).isAfter(originalUpdatedAt); // Should be updated
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Null User Does Nothing")
    void updateUserFromDTO_NullUser_DoesNothing() {
        // When/Then - Should not throw exception
        assertDoesNotThrow(() -> MapperUtils.updateUserFromDTO(null, updateRequestDTO));
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Null DTO Does Nothing")
    void updateUserFromDTO_NullDTO_DoesNothing() {
        // Given
        String originalName = sampleUser.getName();

        // When
        MapperUtils.updateUserFromDTO(sampleUser, null);

        // Then
        assertThat(sampleUser.getName()).isEqualTo(originalName); // Should not change
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Partial Updates Only Change Specified Fields")
    void updateUserFromDTO_PartialUpdates_OnlyChangesSpecifiedFields() {
        // Given
        UserUpdateRequestDTO partialDTO = new UserUpdateRequestDTO();
        partialDTO.setName("Only Name Changed");
        // email and walletBalance are null
        
        String originalEmail = sampleUser.getEmail();
        BigDecimal originalBalance = sampleUser.getWalletBalance();

        // When
        MapperUtils.updateUserFromDTO(sampleUser, partialDTO);

        // Then
        assertThat(sampleUser.getName()).isEqualTo("Only Name Changed");
        assertThat(sampleUser.getEmail()).isEqualTo(originalEmail); // Should not change
        assertThat(sampleUser.getWalletBalance()).isEqualByComparingTo(originalBalance); // Should not change
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Wallet Adjustment Positive")
    void updateUserFromDTO_WalletAdjustmentPositive_IncreasesBalance() {
        // Given
        BigDecimal originalBalance = sampleUser.getWalletBalance(); // 500.75
        BigDecimal adjustment = BigDecimal.valueOf(100.25);
        
        updateRequestDTO.setWalletBalance(null); // Don't set absolute balance
        updateRequestDTO.setWalletAdjustment(adjustment);

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        BigDecimal expectedBalance = originalBalance.add(adjustment); // 601.00
        assertThat(sampleUser.getWalletBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Wallet Adjustment Negative Valid")
    void updateUserFromDTO_WalletAdjustmentNegativeValid_DecreasesBalance() {
        // Given
        BigDecimal originalBalance = sampleUser.getWalletBalance(); // 500.75
        BigDecimal adjustment = BigDecimal.valueOf(-100.00);
        
        updateRequestDTO.setWalletBalance(null);
        updateRequestDTO.setWalletAdjustment(adjustment);

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        BigDecimal expectedBalance = originalBalance.add(adjustment); // 400.75
        assertThat(sampleUser.getWalletBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Wallet Adjustment Insufficient Funds Throws Exception")
    void updateUserFromDTO_WalletAdjustmentInsufficientFunds_ThrowsException() {
        // Given
        BigDecimal originalBalance = sampleUser.getWalletBalance(); // 500.75
        BigDecimal adjustment = BigDecimal.valueOf(-600.00); // More than available
        
        updateRequestDTO.setWalletBalance(null);
        updateRequestDTO.setWalletAdjustment(adjustment);

        // When/Then
        assertThatThrownBy(() -> MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO))
                .isInstanceOf(ExceptionUtils.InsufficientFundsException.class);
        
        // Balance should remain unchanged
        assertThat(sampleUser.getWalletBalance()).isEqualByComparingTo(originalBalance);
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Funding Request IDs Added Without Duplicates")
    void updateUserFromDTO_FundingRequestIds_AddedWithoutDuplicates() {
        // Given
        List<String> originalIds = new ArrayList<>(Arrays.asList("req1", "req2"));
        sampleUser.setFundingRequestIds(originalIds);
        
        updateRequestDTO.setFundingRequestIds(Arrays.asList("req2", "req3", "req4")); // req2 is duplicate

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        List<String> expectedIds = Arrays.asList("req1", "req2", "req3", "req4");
        assertThat(sampleUser.getFundingRequestIds()).containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @DisplayName("ToUserCreationResponseDTO - Valid User")
    void toUserCreationResponseDTO_ValidUser_ReturnsDTO() {
        // When
        UserListResponseDTO dto = MapperUtils.toUserCreationResponseDTO(sampleUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(sampleUser.getId());
        assertThat(dto.getName()).isEqualTo(sampleUser.getName());
        assertThat(dto.getRoles()).isEqualTo(sampleUser.getRoles());
        assertThat(dto.getWalletBalance()).isEqualByComparingTo(sampleUser.getWalletBalance());
    }

    @Test
    @DisplayName("ToUserCreationResponseDTO - Null User Returns Null")
    void toUserCreationResponseDTO_NullUser_ReturnsNull() {
        // When
        UserListResponseDTO dto = MapperUtils.toUserCreationResponseDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("ToUserDetailResponseDTO - Valid User")
    void toUserDetailResponseDTO_ValidUser_ReturnsDTO() {
        // When
        UserResponseDTO dto = MapperUtils.toUserDetailResponseDTO(sampleUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(sampleUser.getId());
        assertThat(dto.getName()).isEqualTo(sampleUser.getName());
        assertThat(dto.getEmail()).isEqualTo(sampleUser.getEmail());
        assertThat(dto.getRoles()).isEqualTo(sampleUser.getRoles());
        assertThat(dto.getWalletBalance()).isEqualByComparingTo(sampleUser.getWalletBalance());
        assertThat(dto.getFundingRequestIds()).isEqualTo(sampleUser.getFundingRequestIds());
        assertThat(dto.getCreatedAt()).isEqualTo(sampleUser.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(sampleUser.getUpdatedAt());
    }

    @Test
    @DisplayName("ToUserDetailResponseDTO - Null User Returns Null")
    void toUserDetailResponseDTO_NullUser_ReturnsNull() {
        // When
        UserResponseDTO dto = MapperUtils.toUserDetailResponseDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Empty String Fields Not Updated")
    void updateUserFromDTO_EmptyStringFields_NotUpdated() {
        // Given
        String originalName = sampleUser.getName();
        String originalEmail = sampleUser.getEmail();
        
        updateRequestDTO.setName(""); // Empty string
        updateRequestDTO.setEmail(""); // Empty string

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        assertThat(sampleUser.getName()).isEqualTo(originalName); // Should not change
        assertThat(sampleUser.getEmail()).isEqualTo(originalEmail); // Should not change
    }

    @Test
    @DisplayName("UpdateUserFromDTO - Whitespace Only Fields Not Updated")
    void updateUserFromDTO_WhitespaceOnlyFields_NotUpdated() {
        // Given
        String originalName = sampleUser.getName();
        String originalEmail = sampleUser.getEmail();
        
        updateRequestDTO.setName("   "); // Whitespace only
        updateRequestDTO.setEmail("   "); // Whitespace only

        // When
        MapperUtils.updateUserFromDTO(sampleUser, updateRequestDTO);

        // Then
        assertThat(sampleUser.getName()).isEqualTo(originalName); // Should not change
        assertThat(sampleUser.getEmail()).isEqualTo(originalEmail); // Should not change
    }
}
