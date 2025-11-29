package com.nexus.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // ============================
    // USER SERVICE FALLBACK
    // ============================
    @GetMapping("/users")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service is currently unavailable. Our developers are working to fix the issue.");
    }

    // ============================
    // ORDER SERVICE FALLBACK
    // ============================
    @GetMapping("/orders")
    public ResponseEntity<String> orderServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order Service is currently unavailable. Our developers are working to fix the issue.");
    }

    // ============================
    // INVENTORY SERVICE FALLBACK
    // ============================
    @GetMapping("/products")
    public ResponseEntity<String> inventoryServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Product Service is currently unavailable. (Our developers are working to fix the issue.");
    }

    // ============================
    // PAYMENT SERVICE FALLBACK
    // ============================
    @GetMapping("/funding-requests")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Investment Service is currently unavailable. Our developers are working to fix the issue.");
    }
}
