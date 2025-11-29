package com.razz.orderservice.command.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.razz.orderservice.command.service.OrderCommandService;
import com.razz.orderservice.dto.OrderResponse;
import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.dto.StatusRequest;
import com.razz.orderservice.dto.StatusResponse;

@ExtendWith(MockitoExtension.class)
class OrderCommandControllerTest {

    @Mock
    private OrderCommandService orderCommandService;

    @InjectMocks
    private OrderCommandController orderCommandController;

    private PlaceOrderRequest placeOrderRequest;
    private StatusRequest statusRequest;

    @BeforeEach
    void setUp() {
        placeOrderRequest = new PlaceOrderRequest(
                "product123",
                5,
                "funder456",
                "supplier789",
                "request001"
        );
        statusRequest = new StatusRequest("DELIVERED");
    }

    @Test
    void place_ShouldReturnOrderResponse_WhenOrderIsPlacedSuccessfully() {
        // Arrange
        String expectedOrderId = "order123";
        when(orderCommandService.place(any(PlaceOrderRequest.class))).thenReturn(expectedOrderId);

        // Act
        ResponseEntity<OrderResponse> response = orderCommandController.place(placeOrderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedOrderId, response.getBody().id());
        assertEquals("PLACED", response.getBody().status());
        verify(orderCommandService, times(1)).place(placeOrderRequest);
    }

    @Test
    void place_ShouldThrowException_WhenServiceThrowsException() {
        // Arrange
        when(orderCommandService.place(any(PlaceOrderRequest.class)))
                .thenThrow(new IllegalArgumentException("Product not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderCommandController.place(placeOrderRequest);
        });
        verify(orderCommandService, times(1)).place(placeOrderRequest);
    }

    @Test
    void updateStatus_ShouldReturnStatusResponse_WhenStatusIsUpdatedSuccessfully() {
        // Arrange
        String orderId = "order123";
        String expectedStatus = "DELIVERED";
        when(orderCommandService.updateStatus(eq(orderId), eq("DELIVERED"))).thenReturn(expectedStatus);

        // Act
        ResponseEntity<StatusResponse> response = orderCommandController.updateStatus(orderId, statusRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedStatus, response.getBody().status());
        verify(orderCommandService, times(1)).updateStatus(orderId, "DELIVERED");
    }

    @Test
    void updateStatus_ShouldThrowException_WhenServiceThrowsException() {
        // Arrange
        String orderId = "order123";
        when(orderCommandService.updateStatus(eq(orderId), eq("DELIVERED")))
                .thenThrow(new IllegalArgumentException("Order not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderCommandController.updateStatus(orderId, statusRequest);
        });
        verify(orderCommandService, times(1)).updateStatus(orderId, "DELIVERED");
    }

    @Test
    void place_ShouldHandleRuntimeException() {
        // Arrange
        when(orderCommandService.place(any(PlaceOrderRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderCommandController.place(placeOrderRequest);
        });
        verify(orderCommandService, times(1)).place(placeOrderRequest);
    }

    @Test
    void updateStatus_ShouldHandleRuntimeException() {
        // Arrange
        String orderId = "order123";
        when(orderCommandService.updateStatus(eq(orderId), eq("DELIVERED")))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderCommandController.updateStatus(orderId, statusRequest);
        });
        verify(orderCommandService, times(1)).updateStatus(orderId, "DELIVERED");
    }
}
