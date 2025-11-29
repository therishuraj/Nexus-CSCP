package com.razz.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FundingRequestResponse(
    String id,
    String funderId,
    String productId,
    int quantity,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<InvestmentDTO> investments
) {
    public record InvestmentDTO(
        String investorId,
        BigDecimal amount,
        LocalDateTime investedAt
    ) {}
}
