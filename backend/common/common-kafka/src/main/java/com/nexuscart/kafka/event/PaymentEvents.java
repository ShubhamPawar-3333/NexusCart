package com.nexuscart.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Events for Kafka messaging
 */
public class PaymentEvents {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCompletedEvent {
        private String eventId;
        private String paymentId;
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionId;
        private LocalDateTime completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentFailedEvent {
        private String eventId;
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private String reason;
        private LocalDateTime failedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundProcessedEvent {
        private String eventId;
        private String refundId;
        private String orderId;
        private String paymentId;
        private BigDecimal amount;
        private LocalDateTime processedAt;
    }
}
