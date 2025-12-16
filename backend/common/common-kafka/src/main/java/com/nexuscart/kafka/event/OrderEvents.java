package com.nexuscart.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Events for Kafka messaging
 */
public class OrderEvents {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCreatedEvent {
        private String eventId;
        private String orderId;
        private String userId;
        private List<OrderItem> items;
        private BigDecimal totalAmount;
        private String shippingAddress;
        private LocalDateTime createdAt;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrderItem {
            private String productId;
            private Integer quantity;
            private BigDecimal price;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderConfirmedEvent {
        private String eventId;
        private String orderId;
        private String userId;
        private BigDecimal totalAmount;
        private LocalDateTime confirmedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCancelledEvent {
        private String eventId;
        private String orderId;
        private String userId;
        private String reason;
        private LocalDateTime cancelledAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCompletedEvent {
        private String eventId;
        private String orderId;
        private String userId;
        private LocalDateTime completedAt;
    }
}
