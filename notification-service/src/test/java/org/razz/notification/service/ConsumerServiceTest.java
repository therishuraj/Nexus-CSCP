package org.razz.notification.service;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for ConsumerService
 * 
 * Tests cover:
 * - Email message consumption from "events" topic
 * - Order notification consumption from "orderNotification" topic
 * - Email validation and error handling
 * - SMTP failure scenarios
 */
@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ConsumerService consumerService;

    private static final String FROM_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(consumerService, "fromEmail", FROM_EMAIL);
    }

    // =========================================================================
    // Generic Email Message Tests (events topic)
    // =========================================================================

    @Test
    void testConsumeValidEmailMessage() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "recipient@example.com");
        message.put("subject", "Test Subject");
        message.put("body", "Test Body Content");

        // Act
        consumerService.consume(message);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo(FROM_EMAIL);
        assertThat(sentMessage.getTo()).containsExactly("recipient@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Test Subject");
        assertThat(sentMessage.getText()).isEqualTo("Test Body Content");
    }

    @Test
    void testConsumeEmailMessageWithMissingEmail() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("subject", "Test Subject");
        message.put("body", "Test Body");

        // Act
        consumerService.consume(message);

        // Assert - Email should not be sent when email field is missing
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeEmailMessageWithEmptyEmail() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "   ");
        message.put("subject", "Test Subject");
        message.put("body", "Test Body");

        // Act
        consumerService.consume(message);

        // Assert - Email should not be sent when email is empty
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeEmailMessageWithMissingSubject() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "recipient@example.com");
        message.put("body", "Test Body");

        // Act
        consumerService.consume(message);

        // Assert - Email should be sent with default subject
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getSubject()).isEqualTo("Notification");
    }

    @Test
    void testConsumeEmailMessageWithMissingBody() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "recipient@example.com");
        message.put("subject", "Test Subject");

        // Act
        consumerService.consume(message);

        // Assert - Email should be sent with empty body
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getText()).isEmpty();
    }

    @Test
    void testConsumeEmailMessageWithMailException() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "recipient@example.com");
        message.put("subject", "Test Subject");
        message.put("body", "Test Body");

        doThrow(new MailException("SMTP connection failed") {})
            .when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        consumerService.consume(message);

        // Assert - Method should handle exception gracefully
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // =========================================================================
    // Order Notification Tests (orderNotification topic)
    // =========================================================================

    @Test
    void testConsumeOrderNotificationValid() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("email", "customer@example.com");
        message.put("subject", "Order Confirmation");
        message.put("body", "Your order has been placed successfully");
        message.put("timestamp", "2024-11-21T10:30:00Z");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo(FROM_EMAIL);
        assertThat(sentMessage.getTo()).containsExactly("customer@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Order Confirmation");
        assertThat(sentMessage.getText()).isEqualTo("Your order has been placed successfully");
    }

    @Test
    void testConsumeOrderNotificationWithMissingOrderId() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("email", "customer@example.com");
        message.put("subject", "Order Confirmation");
        message.put("body", "Your order has been placed");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Email should not be sent when orderId is missing
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeOrderNotificationWithEmptyOrderId() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "   ");
        message.put("email", "customer@example.com");
        message.put("subject", "Order Confirmation");
        message.put("body", "Your order has been placed");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Email should not be sent when orderId is empty
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeOrderNotificationWithMissingEmail() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("subject", "Order Confirmation");
        message.put("body", "Your order has been placed");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Email should not be sent when email is missing
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeOrderNotificationWithMissingSubject() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("email", "customer@example.com");
        message.put("body", "Your order has been placed");
        message.put("timestamp", "2024-11-21T10:30:00Z");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Email should be sent with default subject
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getSubject()).isEqualTo("Order Notification");
    }

    @Test
    void testConsumeOrderNotificationWithMissingBody() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("email", "customer@example.com");
        message.put("subject", "Order Confirmation");
        message.put("timestamp", "2024-11-21T10:30:00Z");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Email should be sent with default body
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getText()).isEqualTo("Your order has been processed.");
    }

    @Test
    void testConsumeOrderNotificationWithMailException() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("email", "customer@example.com");
        message.put("subject", "Order Confirmation");
        message.put("body", "Your order has been placed");
        message.put("timestamp", "2024-11-21T10:30:00Z");

        doThrow(new MailException("SMTP server unavailable") {})
            .when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert - Method should handle exception gracefully
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testConsumeOrderNotificationWithAllFields() {
        // Arrange
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", "507f1f77bcf86cd799439011");
        message.put("email", "customer@example.com");
        message.put("subject", "Order #12345 Confirmed");
        message.put("body", "Dear Customer, your order #12345 has been confirmed and will be shipped soon.");
        message.put("timestamp", "2024-11-21T10:30:45.123Z");

        // Act
        consumerService.consumeOrderNotification(message);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo(FROM_EMAIL);
        assertThat(sentMessage.getTo()).containsExactly("customer@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Order #12345 Confirmed");
        assertThat(sentMessage.getText()).isEqualTo("Dear Customer, your order #12345 has been confirmed and will be shipped soon.");
    }
}
