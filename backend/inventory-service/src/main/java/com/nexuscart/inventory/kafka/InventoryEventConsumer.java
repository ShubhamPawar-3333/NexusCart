package com.nexuscart.inventory.kafka;

import com.nexuscart.inventory.service.InventoryService;
import com.nexuscart.kafka.event.InventoryEvents;
import com.nexuscart.kafka.event.OrderEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void handleOrderCreated(OrderEvents.OrderCreatedEvent event) {
        log.info("Received order.created event for order: {}", event.getOrderId());

        try {
            List<InventoryService.ReservationRequest> requests = event.getItems().stream()
                    .map(item -> new InventoryService.ReservationRequest(
                            UUID.fromString(item.getProductId()),
                            item.getQuantity()))
                    .collect(Collectors.toList());

            var reservations = inventoryService.reserveStock(event.getOrderId(), requests);

            // Publish success event
            InventoryEvents.InventoryReservedEvent successEvent = InventoryEvents.InventoryReservedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .items(reservations.stream()
                            .map(r -> InventoryEvents.InventoryReservedEvent.ReservedItem.builder()
                                    .productId(r.getProductId().toString())
                                    .quantity(r.getQuantity())
                                    .warehouseId(r.getWarehouseId().toString())
                                    .build())
                            .collect(Collectors.toList()))
                    .reservedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("inventory.reserved", successEvent);
            log.info("Published inventory.reserved event for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to reserve inventory for order: {}", event.getOrderId(), e);

            // Publish failure event
            InventoryEvents.InventoryReservationFailedEvent failureEvent = InventoryEvents.InventoryReservationFailedEvent
                    .builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .reason(e.getMessage())
                    .failedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("inventory.failed", failureEvent);
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-service")
    public void handleOrderCancelled(OrderEvents.OrderCancelledEvent event) {
        log.info("Received order.cancelled event for order: {}", event.getOrderId());

        try {
            inventoryService.rollbackReservations(event.getOrderId());

            InventoryEvents.InventoryReleasedEvent releasedEvent = InventoryEvents.InventoryReleasedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .releasedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("inventory.released", releasedEvent);
            log.info("Released inventory for cancelled order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to release inventory for order: {}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "payment.completed", groupId = "inventory-service")
    public void handlePaymentCompleted(Object event) {
        // Payment completed - confirm the reservation
        log.info("Received payment.completed event");
        // Extract order ID and confirm reservation
    }
}
