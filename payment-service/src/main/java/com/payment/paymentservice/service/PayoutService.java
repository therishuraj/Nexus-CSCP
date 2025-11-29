package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.CreatePayoutRequest;
import com.payment.paymentservice.dto.CreatePayoutResponse;
import com.payment.paymentservice.model.Payout;
import com.payment.paymentservice.model.PayoutStatus;
import com.payment.paymentservice.repository.PayoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

// @Slf4j
@Service
// @RequiredArgsConstructor
public class PayoutService {

    private static final Logger log = LoggerFactory.getLogger(PayoutService.class);

    private final PayoutRepository payoutRepository;
    private final UserServiceClient userServiceClient;

        @Value("${payment.admin.user-id}")
        private String adminUserId;

    // Constructor to replace @RequiredArgsConstructor
    public PayoutService(PayoutRepository payoutRepository, UserServiceClient userServiceClient) {
        this.payoutRepository = payoutRepository;
        this.userServiceClient = userServiceClient;
    }

    public CreatePayoutResponse createPayout(CreatePayoutRequest req) {

        log.info("Starting payout. userId={}, amount={}, upiId={}",
                req.getExternalUserId(), req.getAmount(), req.getUpiId());

        // 1) Create payout entry with INITIATED
        Payout payout = Payout.builder()
                .externalUserId(req.getExternalUserId())
                .amount(req.getAmount())
                .upiId(req.getUpiId())
                .status(PayoutStatus.INITIATED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        payout = payoutRepository.save(payout);
        log.info("Payout record created with id={} and status={}", payout.getId(), payout.getStatus());

        // 2) (Real world) call payment provider payout API.
        // For demo: simulate success
        log.info("Simulating payout success in demo mode.");

        // 3) Mark as SUCCESS
        payout.setStatus(PayoutStatus.SUCCESS);
        payout.setUpdatedAt(Instant.now());
        payout = payoutRepository.save(payout);
        log.info("Payout id={} marked as SUCCESS", payout.getId());

        // 4) Adjust wallets:
        //    - Deduct from admin
        userServiceClient.adjustUserWallet(adminUserId, -req.getAmount());
        //    - Add to user
        userServiceClient.adjustUserWallet(req.getExternalUserId(), -req.getAmount());

        // 5) Return response
        return new CreatePayoutResponse(
                payout.getId(),
                payout.getExternalUserId(),
                payout.getAmount(),
                payout.getUpiId(),
                payout.getStatus().name(),
                "Payout processed successfully."
        );
    }
}
