package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePayoutRequest;
import com.payment.paymentservice.dto.CreatePayoutResponse;
import com.payment.paymentservice.service.PayoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayoutController Basic Unit Tests")
class PayoutControllerTest {

    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private PayoutController payoutController;

    private CreatePayoutRequest payoutRequest;
    private CreatePayoutResponse payoutResponse;

    @BeforeEach
    void setUp() {
        // Create sample payout request
        payoutRequest = new CreatePayoutRequest();
        payoutRequest.setExternalUserId("507f1f77bcf86cd799439011");
        payoutRequest.setAmount(250.00);
        payoutRequest.setUpiId("user@paytm");

        // Create sample payout response
        payoutResponse = new CreatePayoutResponse();
        payoutResponse.setPayoutId("pout_MnzJBBTmzqDjFG");
        payoutResponse.setExternalUserId("507f1f77bcf86cd799439011");
        payoutResponse.setAmount(250.00);
        payoutResponse.setUpiId("user@paytm");
        payoutResponse.setStatus("SUCCESS");
        payoutResponse.setMessage("Payout processed successfully");
    }

    @Test
    @DisplayName("Create Payout - Success")
    void createPayout_Success() {
        // Given
        when(payoutService.createPayout(any(CreatePayoutRequest.class)))
                .thenReturn(payoutResponse);

        // When
        ResponseEntity<CreatePayoutResponse> result = payoutController.createPayout(payoutRequest);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("pout_MnzJBBTmzqDjFG", result.getBody().getPayoutId());
        assertEquals("507f1f77bcf86cd799439011", result.getBody().getExternalUserId());
        assertEquals(250.00, result.getBody().getAmount());
        assertEquals("user@paytm", result.getBody().getUpiId());
        assertEquals("SUCCESS", result.getBody().getStatus());
        assertEquals("Payout processed successfully", result.getBody().getMessage());

        verify(payoutService).createPayout(any(CreatePayoutRequest.class));
    }

    @Test
    @DisplayName("Create Payout - Service Exception")
    void createPayout_ServiceException() {
        // Given
        when(payoutService.createPayout(any(CreatePayoutRequest.class)))
                .thenThrow(new RuntimeException("Payout processing failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            payoutController.createPayout(payoutRequest);
        });

        verify(payoutService).createPayout(any(CreatePayoutRequest.class));
    }
}
