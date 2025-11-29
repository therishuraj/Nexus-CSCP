package com.razz.orderservice.dto;

public record ProductResponse(
        String id,
        String name,
        String category,
        int quantity,
        double price,
        String supplierId
) {}
