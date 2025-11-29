package com.razz.orderservice.dto;

public record ProductAvailabilityResponse(
        boolean available,
        double price,
        String message
) {}
