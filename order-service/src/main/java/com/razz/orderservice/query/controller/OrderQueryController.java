package com.razz.orderservice.query.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.query.service.OrderQueryService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    private static final Logger log = LoggerFactory.getLogger(OrderQueryController.class);
    
    private final OrderQueryService service;
    
    public OrderQueryController(OrderQueryService service) { 
        this.service = service; 
    }

    @GetMapping
    public List<OrderView> getByUser(@RequestParam String userId) {
        log.info("Fetching orders for user - UserId: {}", userId);
        long startTime = System.currentTimeMillis();
        
        try {
            List<OrderView> orders = service.getByUser(userId);
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Retrieved {} orders for user - UserId: {}, ExecutionTime: {}ms", 
                    orders.size(), userId, executionTime);
            return orders;
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to retrieve orders for user - UserId: {}, Error: {}, ExecutionTime: {}ms", 
                    userId, e.getMessage(), executionTime, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public OrderView getById(@PathVariable String id) {
        log.info("Fetching order by ID - OrderId: {}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            OrderView order = service.getById(id);
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (order != null) {
                log.info("Retrieved order - OrderId: {}, Status: {}, ExecutionTime: {}ms", 
                        id, order.getStatus(), executionTime);
            } else {
                log.info("Order not found - OrderId: {}, ExecutionTime: {}ms", id, executionTime);
            }
            return order;
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to retrieve order - OrderId: {}, Error: {}, ExecutionTime: {}ms", 
                    id, e.getMessage(), executionTime, e);
            throw e;
        }
    }
}