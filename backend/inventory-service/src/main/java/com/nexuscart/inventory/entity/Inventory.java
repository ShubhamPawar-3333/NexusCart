package com.nexuscart.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inventory Entity for stock tracking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "warehouse_id" })
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    private Integer reorderLevel = 10;

    private Integer maxStock = 1000;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public enum StockStatus {
        IN_STOCK, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED
    }
}
