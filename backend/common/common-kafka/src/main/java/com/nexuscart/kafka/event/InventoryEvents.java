package com.nexuscart.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Inventory Events for Kafka messaging
 */
public class InventoryEvents {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReservedEvent {
        private String eventId;
        private String orderId;
        private List<ReservedItem> items;
        private LocalDateTime reservedAt;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ReservedItem {
            private String productId;
            private Integer quantity;
            private String warehouseId;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReservationFailedEvent {
        private String eventId;
        private String orderId;
        private String reason;
        private List<String> failedProducts;
        private LocalDateTime failedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReleasedEvent {
        private String eventId;
        private String orderId;
        private LocalDateTime releasedAt;
    }
}
