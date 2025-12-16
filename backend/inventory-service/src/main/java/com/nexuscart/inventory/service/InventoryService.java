package com.nexuscart.inventory.service;

import com.nexuscart.inventory.entity.Inventory;
import com.nexuscart.inventory.entity.StockReservation;
import com.nexuscart.inventory.repository.InventoryRepository;
import com.nexuscart.inventory.repository.StockReservationRepository;
import com.nexuscart.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;
    private final WarehouseRepository warehouseRepository;

    public List<Inventory> getProductInventory(UUID productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public Integer getAvailableQuantity(UUID productId) {
        Integer total = inventoryRepository.getTotalAvailableQuantity(productId);
        return total != null ? total : 0;
    }

    public boolean isInStock(UUID productId, int quantity) {
        return getAvailableQuantity(productId) >= quantity;
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Transactional
    public Inventory addStock(UUID productId, UUID warehouseId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .warehouseId(warehouseId)
                        .quantity(0)
                        .reservedQuantity(0)
                        .status(Inventory.StockStatus.IN_STOCK)
                        .build());

        inventory.setQuantity(inventory.getQuantity() + quantity);
        updateStockStatus(inventory);

        log.info("Added {} units to product {} in warehouse {}", quantity, productId, warehouseId);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateStock(UUID productId, UUID warehouseId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQuantity(quantity);
        updateStockStatus(inventory);

        log.info("Updated stock to {} for product {} in warehouse {}", quantity, productId, warehouseId);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public List<StockReservation> reserveStock(String orderId, List<ReservationRequest> items) {
        List<StockReservation> reservations = new ArrayList<>();

        for (ReservationRequest item : items) {
            List<Inventory> availableInventory = inventoryRepository.findAvailableStock(item.productId());

            int remainingQty = item.quantity();

            for (Inventory inv : availableInventory) {
                if (remainingQty <= 0)
                    break;

                int available = inv.getAvailableQuantity();
                int toReserve = Math.min(available, remainingQty);

                if (toReserve > 0) {
                    inv.setReservedQuantity(inv.getReservedQuantity() + toReserve);
                    inventoryRepository.save(inv);

                    StockReservation reservation = StockReservation.builder()
                            .orderId(orderId)
                            .productId(item.productId())
                            .warehouseId(inv.getWarehouseId())
                            .quantity(toReserve)
                            .status(StockReservation.ReservationStatus.PENDING)
                            .expiresAt(LocalDateTime.now().plusMinutes(30))
                            .build();

                    reservations.add(reservationRepository.save(reservation));
                    remainingQty -= toReserve;
                }
            }

            if (remainingQty > 0) {
                // Rollback previous reservations
                rollbackReservations(orderId);
                throw new RuntimeException("Insufficient stock for product: " + item.productId());
            }
        }

        log.info("Reserved stock for order: {}", orderId);
        return reservations;
    }

    @Transactional
    public void confirmReservation(String orderId) {
        List<StockReservation> reservations = reservationRepository
                .findByOrderIdAndStatus(orderId, StockReservation.ReservationStatus.PENDING);

        for (StockReservation reservation : reservations) {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(reservation.getProductId(), reservation.getWarehouseId())
                    .orElseThrow();

            inventory.setQuantity(inventory.getQuantity() - reservation.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            updateStockStatus(inventory);
            inventoryRepository.save(inventory);

            reservation.setStatus(StockReservation.ReservationStatus.CONFIRMED);
            reservation.setConfirmedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
        }

        log.info("Confirmed reservation for order: {}", orderId);
    }

    @Transactional
    public void rollbackReservations(String orderId) {
        List<StockReservation> reservations = reservationRepository
                .findByOrderIdAndStatus(orderId, StockReservation.ReservationStatus.PENDING);

        for (StockReservation reservation : reservations) {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(reservation.getProductId(), reservation.getWarehouseId())
                    .orElseThrow();

            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            updateStockStatus(inventory);
            inventoryRepository.save(inventory);

            reservation.setStatus(StockReservation.ReservationStatus.CANCELLED);
            reservation.setCancelledAt(LocalDateTime.now());
            reservationRepository.save(reservation);
        }

        log.info("Rolled back reservations for order: {}", orderId);
    }

    private void updateStockStatus(Inventory inventory) {
        int available = inventory.getAvailableQuantity();

        if (available <= 0) {
            inventory.setStatus(Inventory.StockStatus.OUT_OF_STOCK);
        } else if (available <= inventory.getReorderLevel()) {
            inventory.setStatus(Inventory.StockStatus.LOW_STOCK);
        } else {
            inventory.setStatus(Inventory.StockStatus.IN_STOCK);
        }
    }

    public record ReservationRequest(UUID productId, int quantity) {
    }
}
