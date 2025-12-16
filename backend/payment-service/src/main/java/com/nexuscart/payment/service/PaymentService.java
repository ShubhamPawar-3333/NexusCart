package com.nexuscart.payment.service;

import com.nexuscart.kafka.event.PaymentEvents;
import com.nexuscart.payment.entity.Payment;
import com.nexuscart.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getUserPayments(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional
    public Payment initiatePayment(String orderId, UUID userId, BigDecimal amount,
            String currency, Payment.PaymentMethod method) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .method(method)
                .status(Payment.PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Initiated payment for order: {}", orderId);

        return payment;
    }

    @Transactional
    public Payment processPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setStatus(Payment.PaymentStatus.PROCESSING);

        // Simulate payment gateway processing
        try {
            Thread.sleep(1000); // Simulate API call

            // Mock successful payment
            payment.setTransactionId("TXN-" + System.currentTimeMillis());
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            payment.setGatewayResponse("Payment successful");

            payment = paymentRepository.save(payment);

            // Publish payment completed event
            PaymentEvents.PaymentCompletedEvent event = PaymentEvents.PaymentCompletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .paymentId(payment.getId().toString())
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId().toString())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getMethod().name())
                    .transactionId(payment.getTransactionId())
                    .completedAt(payment.getCompletedAt())
                    .build();

            kafkaTemplate.send("payment.completed", event);
            log.info("Payment completed for order: {}", payment.getOrderId());

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            payment = paymentRepository.save(payment);

            PaymentEvents.PaymentFailedEvent failedEvent = PaymentEvents.PaymentFailedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId().toString())
                    .amount(payment.getAmount())
                    .reason(e.getMessage())
                    .failedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("payment.failed", failedEvent);
            log.error("Payment failed for order: {}", payment.getOrderId());
        }

        return payment;
    }

    @Transactional
    public Payment refundPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Can only refund completed payments");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        PaymentEvents.RefundProcessedEvent event = PaymentEvents.RefundProcessedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .refundId(UUID.randomUUID().toString())
                .orderId(payment.getOrderId())
                .paymentId(payment.getId().toString())
                .amount(payment.getAmount())
                .processedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("payment.refunded", event);
        log.info("Refund processed for payment: {}", paymentId);

        return payment;
    }
}
