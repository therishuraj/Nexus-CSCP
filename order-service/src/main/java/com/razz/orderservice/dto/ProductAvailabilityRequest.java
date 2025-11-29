package com.razz.orderservice.dto;

public record ProductAvailabilityRequest(
        String productId,
        int quantity
) {}
