package com.nexuscart.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private String subject;

    @Column(length = 2000)
    private String content;

    private String recipient; // email, phone number, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    private String referenceId; // orderId, paymentId, etc.

    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    public enum NotificationType {
        ORDER_CONFIRMATION, ORDER_SHIPPED, ORDER_DELIVERED, ORDER_CANCELLED,
        PAYMENT_SUCCESS, PAYMENT_FAILED, REFUND_PROCESSED,
        WELCOME, PASSWORD_RESET, ACCOUNT_VERIFIED
    }

    public enum NotificationChannel {
        EMAIL, SMS, PUSH
    }

    public enum NotificationStatus {
        PENDING, SENT, FAILED
    }
}
