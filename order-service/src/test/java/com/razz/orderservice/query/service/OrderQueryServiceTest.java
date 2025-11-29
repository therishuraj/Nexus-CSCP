package com.razz.orderservice.query.service;

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
import com.razz.orderservice.repository.OrderViewRepository;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private OrderViewRepository orderViewRepository;

    @InjectMocks
    private OrderQueryService orderQueryService;

    private OrderView orderView1;
    private OrderView orderView2;

    @BeforeEach
    void setUp() {
        orderView1 = new OrderView();
        orderView1.setOrderId("order123");
        orderView1.setStatus("PLACED");
        orderView1.setFunderName("funder456");
        orderView1.setSupplierName("supplier789");
        orderView1.setProductName("Product A");
        orderView1.setQuantity(5);
        orderView1.setTotalAmount(500.0);

        orderView2 = new OrderView();
        orderView2.setOrderId("order456");
        orderView2.setStatus("DELIVERED");
        orderView2.setFunderName("funder456");
        orderView2.setSupplierName("supplier999");
        orderView2.setProductName("Product B");
        orderView2.setQuantity(3);
        orderView2.setTotalAmount(600.0);
    }

    @Test
    void getByUser_ShouldReturnListOfOrders_WhenOrdersExistForFunder() {
        // Arrange
        String userId = "funder456";
        List<OrderView> expectedOrders = Arrays.asList(orderView1, orderView2);
        when(orderViewRepository.findByFunderNameOrSupplierName(eq(userId), eq(userId)))
                .thenReturn(expectedOrders);

        // Act
        List<OrderView> result = orderQueryService.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("order123", result.get(0).getOrderId());
        assertEquals("order456", result.get(1).getOrderId());
        verify(orderViewRepository, times(1)).findByFunderNameOrSupplierName(userId, userId);
    }

    @Test
    void getByUser_ShouldReturnListOfOrders_WhenOrdersExistForSupplier() {
        // Arrange
        String userId = "supplier789";
        List<OrderView> expectedOrders = List.of(orderView1);
        when(orderViewRepository.findByFunderNameOrSupplierName(eq(userId), eq(userId)))
                .thenReturn(expectedOrders);

        // Act
        List<OrderView> result = orderQueryService.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("order123", result.get(0).getOrderId());
        assertEquals("PLACED", result.get(0).getStatus());
        verify(orderViewRepository, times(1)).findByFunderNameOrSupplierName(userId, userId);
    }

    @Test
    void getByUser_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Arrange
        String userId = "nonexistent";
        when(orderViewRepository.findByFunderNameOrSupplierName(eq(userId), eq(userId)))
                .thenReturn(List.of());

        // Act
        List<OrderView> result = orderQueryService.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderViewRepository, times(1)).findByFunderNameOrSupplierName(userId, userId);
    }

    @Test
    void getById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        String orderId = "order123";
        when(orderViewRepository.findByOrderId(eq(orderId))).thenReturn(orderView1);

        // Act
        OrderView result = orderQueryService.getById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals("order123", result.getOrderId());
        assertEquals("PLACED", result.getStatus());
        assertEquals("funder456", result.getFunderName());
        assertEquals("supplier789", result.getSupplierName());
        assertEquals("Product A", result.getProductName());
        assertEquals(5, result.getQuantity());
        assertEquals(500.0, result.getTotalAmount());
        verify(orderViewRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getById_ShouldReturnNull_WhenOrderDoesNotExist() {
        // Arrange
        String orderId = "nonexistent";
        when(orderViewRepository.findByOrderId(eq(orderId))).thenReturn(null);

        // Act
        OrderView result = orderQueryService.getById(orderId);

        // Assert
        assertNull(result);
        verify(orderViewRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getByUser_ShouldHandleRepositoryException() {
        // Arrange
        String userId = "funder456";
        when(orderViewRepository.findByFunderNameOrSupplierName(eq(userId), eq(userId)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderQueryService.getByUser(userId);
        });
        verify(orderViewRepository, times(1)).findByFunderNameOrSupplierName(userId, userId);
    }

    @Test
    void getById_ShouldHandleRepositoryException() {
        // Arrange
        String orderId = "order123";
        when(orderViewRepository.findByOrderId(eq(orderId)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderQueryService.getById(orderId);
        });
        verify(orderViewRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getByUser_ShouldReturnCorrectOrderDetails() {
        // Arrange
        String userId = "funder456";
        when(orderViewRepository.findByFunderNameOrSupplierName(eq(userId), eq(userId)))
                .thenReturn(List.of(orderView1));

        // Act
        List<OrderView> result = orderQueryService.getByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        OrderView order = result.get(0);
        assertEquals("Product A", order.getProductName());
        assertEquals(5, order.getQuantity());
        assertEquals(500.0, order.getTotalAmount());
    }
}
