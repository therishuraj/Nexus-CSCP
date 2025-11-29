package com.razz.orderservice.query.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.query.service.OrderQueryService;

@ExtendWith(MockitoExtension.class)
class OrderQueryControllerTest {

    @Mock
    private OrderQueryService orderQueryService;

    @InjectMocks
    private OrderQueryController orderQueryController;

    private OrderView orderView1;
    private OrderView orderView2;

    @BeforeEach
    void setUp() {
        orderView1 = new OrderView();
        orderView1.setOrderId("order123");
        orderView1.setStatus("PLACED");
        orderView1.setFunderName("funder456");
        orderView1.setSupplierName("supplier789");
        
        orderView2 = new OrderView();
        orderView2.setOrderId("order456");
        orderView2.setStatus("DELIVERED");
        orderView2.setFunderName("funder456");
        orderView2.setSupplierName("supplier999");
    }

    @Test
    void getByUser_ShouldReturnListOfOrders_WhenOrdersExist() {
        // Arrange
        String userId = "funder456";
        List<OrderView> expectedOrders = Arrays.asList(orderView1, orderView2);
        when(orderQueryService.getByUser(eq(userId))).thenReturn(expectedOrders);

        // Act
        List<OrderView> result = orderQueryController.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("order123", result.get(0).getOrderId());
        assertEquals("order456", result.get(1).getOrderId());
        verify(orderQueryService, times(1)).getByUser(userId);
    }

    @Test
    void getByUser_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Arrange
        String userId = "nonexistent";
        when(orderQueryService.getByUser(eq(userId))).thenReturn(List.of());

        // Act
        List<OrderView> result = orderQueryController.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderQueryService, times(1)).getByUser(userId);
    }

    @Test
    void getById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        String orderId = "order123";
        when(orderQueryService.getById(eq(orderId))).thenReturn(orderView1);

        // Act
        OrderView result = orderQueryController.getById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals("order123", result.getOrderId());
        assertEquals("PLACED", result.getStatus());
        assertEquals("funder456", result.getFunderName());
        verify(orderQueryService, times(1)).getById(orderId);
    }

    @Test
    void getById_ShouldReturnNull_WhenOrderDoesNotExist() {
        // Arrange
        String orderId = "nonexistent";
        when(orderQueryService.getById(eq(orderId))).thenReturn(null);

        // Act
        OrderView result = orderQueryController.getById(orderId);

        // Assert
        assertNull(result);
        verify(orderQueryService, times(1)).getById(orderId);
    }

    @Test
    void getByUser_ShouldHandleException_WhenServiceThrowsException() {
        // Arrange
        String userId = "funder456";
        when(orderQueryService.getByUser(eq(userId)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderQueryController.getByUser(userId);
        });
        verify(orderQueryService, times(1)).getByUser(userId);
    }

    @Test
    void getById_ShouldHandleException_WhenServiceThrowsException() {
        // Arrange
        String orderId = "order123";
        when(orderQueryService.getById(eq(orderId)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderQueryController.getById(orderId);
        });
        verify(orderQueryService, times(1)).getById(orderId);
    }
}
