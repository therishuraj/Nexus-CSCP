package com.razz.orderservice.command.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razz.orderservice.command.service.OrderCommandService;
import com.razz.orderservice.dto.OrderResponse;
import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.dto.StatusRequest;
import com.razz.orderservice.dto.StatusResponse;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandController.class);
    
    private final OrderCommandService service;
    
    public OrderCommandController(OrderCommandService service) { 
        this.service = service; 
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@RequestBody PlaceOrderRequest cmd) {
        long startTime = System.currentTimeMillis();
        log.info("Received place order request - ProductId: {}, Quantity: {}, FunderId: {}, SupplierId: {}, RequestId: {}", 
                cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        
        try {
            String id = service.place(cmd);
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Order placed successfully - OrderId: {}, ExecutionTime: {}ms", id, executionTime);
            return ResponseEntity.ok(new OrderResponse(id, "PLACED"));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to place order - ProductId: {}, Error: {}, ExecutionTime: {}ms", 
                    cmd.productId(), e.getMessage(), executionTime, e);
            throw e;
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<StatusResponse> updateStatus(@PathVariable String id, @RequestBody StatusRequest cmd) {
        long startTime = System.currentTimeMillis();
        log.info("Received update status request - OrderId: {}, NewStatus: {}", id, cmd.status());
        
        try {
            String status = service.updateStatus(id, cmd.status());
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Order status updated successfully - OrderId: {}, Status: {}, ExecutionTime: {}ms", 
                    id, status, executionTime);
            return ResponseEntity.ok(new StatusResponse(status));
            
        } catch (RuntimeException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to update order status - OrderId: {}, Error: {}, ExecutionTime: {}ms", 
                    id, e.getMessage(), executionTime, e);
            throw e;
        }
    }
}