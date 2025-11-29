package com.nexus.user_service.controller;

import com.nexus.user_service.dto.request.WalletDepositRequestDTO;
import com.nexus.user_service.dto.request.WalletWithdrawRequestDTO;
import com.nexus.user_service.model.User;
import com.nexus.user_service.service.PaymentServiceClient;
import com.nexus.user_service.service.UserService;
import com.nexus.user_service.utils.LoggerUtils;
import com.nexus.user_service.utils.ResponseUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/wallet")
@CrossOrigin(origins = "*")
public class WalletController {
    
    private static final Logger logger = LoggerUtils.getLogger(WalletController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PaymentServiceClient paymentServiceClient;
    
    /**
     * Add money to wallet via payment service
     * POST /api/v1/wallet/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> depositMoney(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody WalletDepositRequestDTO request) {
        
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Wallet deposit request received - User ID: {}, Amount: {}", userId, request.getAmount());
            
            // Validate user ID
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Wallet deposit failed - User ID is required");
                return ResponseEntity.badRequest().body(ResponseUtils.error("User ID header (X-User-Id) is required"));
            }
            
            // Validate amount
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Wallet deposit failed - Invalid amount: {}", request.getAmount());
                return ResponseEntity.badRequest().body(ResponseUtils.error("Amount must be greater than zero"));
            }
            
            // Check if user exists
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                logger.warn("Wallet deposit failed - User not found: {}", userId);
                return ResponseEntity.badRequest().body(ResponseUtils.error("User not found"));
            }
            
            logger.debug("User validation successful, calling payment service for deposit");
            
            // Call payment service
            Map<String, Object> paymentResponse = paymentServiceClient.addMoneyToWallet(userId, request.getAmount());
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Wallet deposit completed - User ID: {}, Amount: {}, Execution time: {}ms", 
                userId, request.getAmount(), executionTime);
            
            return ResponseEntity.ok(paymentResponse);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Wallet deposit failed - User ID: {}, Amount: {}, Error: {}, Execution time: {}ms", 
                userId, request.getAmount(), e.getMessage(), executionTime, e);
            return ResponseEntity.badRequest().body(ResponseUtils.error("Deposit failed: " + e.getMessage()));
        }
    }
    
    /**
     * Withdraw money from wallet via payment service
     * POST /api/v1/wallet/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawMoney(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody WalletWithdrawRequestDTO request) {
        
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Wallet withdrawal request received - User ID: {}, Amount: {}", userId, request.getAmount());
            
            // Validate user ID
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Wallet withdrawal failed - User ID is required");
                return ResponseEntity.badRequest().body(ResponseUtils.error("User ID header (X-User-Id) is required"));
            }
            
            // Validate amount
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Wallet withdrawal failed - Invalid amount: {}", request.getAmount());
                return ResponseEntity.badRequest().body(ResponseUtils.error("Amount must be greater than zero"));
            }
            
            // Check if user exists and get user details
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                logger.warn("Wallet withdrawal failed - User not found: {}", userId);
                return ResponseEntity.badRequest().body(ResponseUtils.error("User not found"));
            }
            
            User user = userOpt.get();
            
            // Generate UPI ID from user name
            String upiId = paymentServiceClient.generateUpiId(user.getName());
            logger.debug("Generated UPI ID for withdrawal - User: {}, UPI ID: {}", user.getName(), upiId);
            
            logger.debug("User validation successful, calling payment service for withdrawal");
            
            // Call payment service
            Map<String, Object> payoutResponse = paymentServiceClient.withdrawMoneyFromWallet(userId, request.getAmount(), upiId);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Wallet withdrawal completed - User ID: {}, Amount: {}, UPI ID: {}, Execution time: {}ms", 
                userId, request.getAmount(), upiId, executionTime);
            
            return ResponseEntity.ok(payoutResponse);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Wallet withdrawal failed - User ID: {}, Amount: {}, Error: {}, Execution time: {}ms", 
                userId, request.getAmount(), e.getMessage(), executionTime, e);
            return ResponseEntity.badRequest().body(ResponseUtils.error("Withdrawal failed: " + e.getMessage()));
        }
    }
}
