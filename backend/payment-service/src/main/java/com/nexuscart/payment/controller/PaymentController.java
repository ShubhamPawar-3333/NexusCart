package com.nexuscart.payment.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.payment.entity.Payment;
import com.nexuscart.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<Payment>> getPayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrder(@PathVariable String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Get user's payments")
    public ResponseEntity<ApiResponse<List<Payment>>> getUserPayments(
            @RequestHeader("X-User-Id") String userId) {
        List<Payment> payments = paymentService.getUserPayments(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment")
    public ResponseEntity<ApiResponse<Payment>> initiatePayment(@RequestBody InitiatePaymentRequest request) {
        Payment payment = paymentService.initiatePayment(
                request.orderId(), request.userId(), request.amount(),
                request.currency(), request.method());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment initiated"));
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process payment")
    public ResponseEntity<ApiResponse<Payment>> processPayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.processPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment processed"));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment")
    public ResponseEntity<ApiResponse<Payment>> refundPayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Refund processed"));
    }

    public record InitiatePaymentRequest(
            String orderId, UUID userId, BigDecimal amount,
            String currency, Payment.PaymentMethod method) {
    }
}
