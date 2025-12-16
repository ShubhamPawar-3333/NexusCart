package com.nexuscart.notification.kafka;

import com.nexuscart.kafka.event.OrderEvents;
import com.nexuscart.kafka.event.PaymentEvents;
import com.nexuscart.notification.entity.Notification;
import com.nexuscart.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order.created", groupId = "notification-service")
    public void handleOrderCreated(OrderEvents.OrderCreatedEvent event) {
        log.info("Received order.created event for order: {}", event.getOrderId());

        notificationService.createAndSendNotification(
                UUID.fromString(event.getUserId()),
                "user@example.com", // Would fetch from user service
                Notification.NotificationType.ORDER_CONFIRMATION,
                Notification.NotificationChannel.EMAIL,
                "Order Confirmed - " + event.getOrderId(),
                "Your order has been placed successfully. Total: " + event.getTotalAmount(),
                event.getOrderId());
    }

    @KafkaListener(topics = "order.cancelled", groupId = "notification-service")
    public void handleOrderCancelled(OrderEvents.OrderCancelledEvent event) {
        log.info("Received order.cancelled event for order: {}", event.getOrderId());

        notificationService.createAndSendNotification(
                UUID.fromString(event.getUserId()),
                "user@example.com",
                Notification.NotificationType.ORDER_CANCELLED,
                Notification.NotificationChannel.EMAIL,
                "Order Cancelled - " + event.getOrderId(),
                "Your order has been cancelled. Reason: " + event.getReason(),
                event.getOrderId());
    }

    @KafkaListener(topics = "payment.completed", groupId = "notification-service")
    public void handlePaymentCompleted(PaymentEvents.PaymentCompletedEvent event) {
        log.info("Received payment.completed event for order: {}", event.getOrderId());

        notificationService.createAndSendNotification(
                UUID.fromString(event.getUserId()),
                "user@example.com",
                Notification.NotificationType.PAYMENT_SUCCESS,
                Notification.NotificationChannel.EMAIL,
                "Payment Successful - " + event.getTransactionId(),
                "Payment of " + event.getAmount() + " completed for order " + event.getOrderId(),
                event.getOrderId());
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service")
    public void handlePaymentFailed(PaymentEvents.PaymentFailedEvent event) {
        log.info("Received payment.failed event for order: {}", event.getOrderId());

        notificationService.createAndSendNotification(
                UUID.fromString(event.getUserId()),
                "user@example.com",
                Notification.NotificationType.PAYMENT_FAILED,
                Notification.NotificationChannel.EMAIL,
                "Payment Failed",
                "Payment failed for order " + event.getOrderId() + ". Reason: " + event.getReason(),
                event.getOrderId());
    }
}
