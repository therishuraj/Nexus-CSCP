package com.razz.orderservice.dto;

public record PlaceOrderRequest(
        String productId,
        int quantity,
        String funderId,
        String supplierId,
        String requestId
) {}