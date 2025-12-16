package com.nexuscart.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock Reservation for order processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_reservations")
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID warehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    private LocalDateTime cancelledAt;

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED, EXPIRED
    }
}
