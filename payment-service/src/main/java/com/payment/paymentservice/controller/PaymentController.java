package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.CreatePaymentRequest;
import com.payment.paymentservice.dto.PaymentInitResponse;
import com.payment.paymentservice.dto.RazorpaySuccessRequest;
import com.payment.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @Slf4j
@RestController
@RequestMapping("/api/v1/deposit")
// @RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    // Constructor to replace @RequiredArgsConstructor
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentInitResponse> createPayment(@RequestBody CreatePaymentRequest req) {
        return ResponseEntity.ok(paymentService.createPayment(req));
    }

    // 2) Called from UI after Razorpay Checkout success
    @PostMapping("/{paymentId}/success")
    public ResponseEntity<PaymentInitResponse> handleSuccess(
            @PathVariable String paymentId,
            @RequestBody RazorpaySuccessRequest dto) {

        return ResponseEntity.ok(paymentService.handleSuccess(paymentId, dto));
    }
}