package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.WalletAdjustmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

// @Slf4j
@Component
// @RequiredArgsConstructor
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);

    private final WebClient userServiceWebClient;

    // Constructor to replace @RequiredArgsConstructor
    public UserServiceClient(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public void adjustUserWallet(String userId, double adjustment) {
        log.info("Calling user-service to adjust wallet. userId={}, adjustment={}", userId, adjustment);

        WalletAdjustmentRequest body = new WalletAdjustmentRequest(adjustment);

        userServiceWebClient.put()
                .uri("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Successfully adjusted wallet for userId={}", userId))
                .doOnError(ex -> log.error("Failed to adjust wallet for userId={}, error={}", userId, ex.getMessage()))
                .block(); 
    }
}
