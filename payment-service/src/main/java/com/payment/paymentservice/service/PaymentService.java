package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.PaymentInitResponse;
import com.payment.paymentservice.dto.CreatePaymentRequest;
import com.payment.paymentservice.dto.RazorpaySuccessRequest;
import com.payment.paymentservice.model.Payment;
import com.payment.paymentservice.model.PaymentStatus;
import com.payment.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

// @Slf4j
@Service
// @RequiredArgsConstructor
public class PaymentService {

        @Value("${razorpay.key-id}")
        private String razorpayKeyId;

        @Value("${razorpay.key-secret}")
        private String razorpayKeySecret;

        @Value("${razorpay.currency:INR}")
        private String currency;

        @Value("${app.ui.base-url:http://localhost:3006}")
        private String uiBaseUrl;

        @Value("${payment.admin.user-id}")
        private String adminUserId;


        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);
        private final PaymentRepository paymentRepository;
        private final WebClient razorpayWebClient;
        private final UserServiceClient userServiceClient;
        public PaymentService(PaymentRepository paymentRepository,
                                  WebClient razorpayWebClient,
                                  UserServiceClient userServiceClient) {
                this.paymentRepository = paymentRepository;
                this.razorpayWebClient = razorpayWebClient;
                this.userServiceClient = userServiceClient;
        }

        // ---------------- PAYMENT START ----------------

    public PaymentInitResponse createPayment(CreatePaymentRequest req) {
        log.info("Starting Razorpay payment. userId={}, amount={}", req.getExternalUserId(), req.getAmount());

        // 1) Save initial payment and Status
        Payment payment = new Payment();
        payment.setExternalUserId(req.getExternalUserId());
        payment.setAmount(req.getAmount());
        payment.setStatus(PaymentStatus.INITIATED);
        payment = paymentRepository.save(payment);

        // 2) Amount in paise
        long amountInPaise = Math.round(req.getAmount() * 100);

        // 3) Call Razorpay Orders API
        Map<String, Object> orderRequest = Map.of(
                "amount", amountInPaise,
                "currency", currency,
                "receipt", payment.getId(),   // any string
                "payment_capture", 1          // auto-capture
        );

        Map<String, Object> orderResponse = razorpayWebClient.post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String razorpayOrderId = (String) orderResponse.get("id");

        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        // 4) Build checkout URL (UI page served by this service)
        String checkoutUrl = uiBaseUrl + "/checkout.html"
        + "?paymentId=" + payment.getId()
        + "&orderId=" + razorpayOrderId
        + "&amount=" + amountInPaise
        + "&key=" + razorpayKeyId;


        log.info("Payment initiated. paymentId={}, razorpayOrderId={}, checkoutUrl={}",
                payment.getId(), razorpayOrderId, checkoutUrl);

        return PaymentInitResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .razorpayOrderId(razorpayOrderId)
                .razorpayKey(razorpayKeyId)
                .amount(req.getAmount())
                .currency(currency)
                .checkoutUrl(checkoutUrl)
                .build();
    }

    // ---------------- PAYMENT SUCCESS CALLBACK ----------------

    public PaymentInitResponse handleSuccess(String paymentId, RazorpaySuccessRequest dto) {
        log.info("Handling Razorpay success callback for paymentId={}", paymentId);

        // 1) Verify signature from Razorpay
        verifySignature(dto.getRazorpayOrderId(), dto.getRazorpayPaymentId(), dto.getRazorpaySignature());

        // 2) Load payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!dto.getRazorpayOrderId().equals(payment.getRazorpayOrderId())) {
            throw new IllegalArgumentException("OrderId mismatch");
        }

        // 3) Update & mark success
        payment.setRazorpayPaymentId(dto.getRazorpayPaymentId());
        payment.setRazorpaySignature(dto.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // 4) Wallet adjustments (same logic as earlier)
        double amount = payment.getAmount();

        log.info("Adjusting user wallet. userId={}, amount={}", payment.getExternalUserId(), amount);
        userServiceClient.adjustUserWallet(payment.getExternalUserId(), amount);

        log.info("Adjusting admin wallet. adminUserId={}, amount={}", adminUserId, amount);
        userServiceClient.adjustUserWallet(adminUserId, amount);

        log.info("Payment flow completed for paymentId={}", paymentId);

        return PaymentInitResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayKey(razorpayKeyId)
                .amount(payment.getAmount())
                .currency(currency)
                .checkoutUrl(null) // not needed now
                .build();
    }

    // ---------------- SIGNATURE VERIFY ----------------

    private void verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String expected = HexFormat.of().formatHex(digest);

            if (!expected.equals(signature)) {
                log.error("Invalid Razorpay signature. expected={}, actual={}", expected, signature);
                throw new IllegalArgumentException("Invalid Razorpay signature");
            }
            log.info("Razorpay signature verified successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Razorpay signature", e);
        }
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }
    
}