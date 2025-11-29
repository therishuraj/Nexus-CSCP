package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePaymentRequest;
import com.payment.paymentservice.dto.PaymentInitResponse;
import com.payment.paymentservice.service.PaymentService;
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
@DisplayName("PaymentController Basic Unit Tests")
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private CreatePaymentRequest paymentRequest;
    private PaymentInitResponse paymentResponse;

    @BeforeEach
    void setUp() {
        // Create sample payment request
        paymentRequest = new CreatePaymentRequest();
        paymentRequest.setExternalUserId("507f1f77bcf86cd799439011");
        paymentRequest.setAmount(500.00);

        // Create sample payment response using builder
        paymentResponse = PaymentInitResponse.builder()
                .paymentId("pay_MnzJBBTmzqDjFG")
                .status("SUCCESS")
                .razorpayOrderId("order_MnzJBBTmzqDjFG")
                .razorpayKey("rzp_test_key")
                .amount(500.00)
                .currency("INR")
                .checkoutUrl("https://checkout.razorpay.com/v1/checkout.js")
                .build();
    }

    @Test
    @DisplayName("Create Payment - Success")
    void createPayment_Success() {
        // Given
        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenReturn(paymentResponse);

        // When
        ResponseEntity<PaymentInitResponse> result = paymentController.createPayment(paymentRequest);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("pay_MnzJBBTmzqDjFG", result.getBody().getPaymentId());
        assertEquals("SUCCESS", result.getBody().getStatus());
        assertEquals(500.00, result.getBody().getAmount());
        assertEquals("order_MnzJBBTmzqDjFG", result.getBody().getRazorpayOrderId());
        assertEquals("rzp_test_key", result.getBody().getRazorpayKey());
        assertEquals("INR", result.getBody().getCurrency());

        verify(paymentService).createPayment(any(CreatePaymentRequest.class));
    }

    @Test
    @DisplayName("Create Payment - Service Exception")
    void createPayment_ServiceException() {
        // Given
        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment processing failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            paymentController.createPayment(paymentRequest);
        });

        verify(paymentService).createPayment(any(CreatePaymentRequest.class));
    }
}
