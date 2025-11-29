package com.nexus.user_service.service;

import com.nexus.user_service.utils.LoggerUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PaymentServiceClient {
    
    private static final Logger logger = LoggerUtils.getLogger(PaymentServiceClient.class);
    
    private final WebClient webClient;
    
    @Value("${payment.service.base-url:http://payment-service:3006}")
    private String paymentServiceBaseUrl;
    
    public PaymentServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    /**
     * Call payment service to add money to wallet
     * @param externalUserId User ID
     * @param amount Amount to add
     * @return Payment service response
     */
    public Map<String, Object> addMoneyToWallet(String externalUserId, BigDecimal amount) {
        try {
            logger.info("Calling payment service for deposit - User ID: {}, Amount: {}", externalUserId, amount);
            
            Map<String, Object> requestBody = Map.of(
                "externalUserId", externalUserId,
                "amount", amount
            );
            
            Map<String, Object> response = webClient.post()
                .uri(paymentServiceBaseUrl + "/api/v1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            logger.info("Payment service deposit response received - User ID: {}, Response: {}", externalUserId, response);
            return response;
            
        } catch (Exception e) {
            logger.error("Payment service deposit call failed - User ID: {}, Amount: {}, Error: {}", 
                externalUserId, amount, e.getMessage(), e);
            throw new RuntimeException("Payment service call failed: " + e.getMessage());
        }
    }
    
    /**
     * Call payment service to withdraw money from wallet
     * @param externalUserId User ID
     * @param amount Amount to withdraw
     * @param upiId UPI ID for withdrawal
     * @return Payout service response
     */
    public Map<String, Object> withdrawMoneyFromWallet(String externalUserId, BigDecimal amount, String upiId) {
        try {
            logger.info("Calling payment service for withdrawal - User ID: {}, Amount: {}, UPI ID: {}", 
                externalUserId, amount, upiId);
            
            Map<String, Object> requestBody = Map.of(
                "externalUserId", externalUserId,
                "amount", amount,
                "upiId", upiId
            );
            
            Map<String, Object> response = webClient.post()
                .uri(paymentServiceBaseUrl + "/api/v1/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            logger.info("Payment service withdrawal response received - User ID: {}, Response: {}", externalUserId, response);
            return response;
            
        } catch (Exception e) {
            logger.error("Payment service withdrawal call failed - User ID: {}, Amount: {}, UPI: {}, Error: {}", 
                externalUserId, amount, upiId, e.getMessage(), e);
            throw new RuntimeException("Payment service call failed: " + e.getMessage());
        }
    }
    
    /**
     * Sanitize user name for UPI ID generation
     * @param userName User's name
     * @return Sanitized UPI ID
     */
    public String generateUpiId(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return "user@upi";
        }
        
        String sanitized = userName.toLowerCase()
            .replaceAll("[^a-z0-9]", "")  // Remove special chars & spaces
            .trim();
        
        if (sanitized.isEmpty()) {
            sanitized = "user";
        }
        
        return sanitized + "@upi";
    }
}
