package com.nexuscart.inventory.repository;

import com.nexuscart.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND i.warehouseId = :warehouseId")
    Optional<Inventory> findByProductIdAndWarehouseIdWithLock(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId);

    Optional<Inventory> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

    List<Inventory> findByProductId(UUID productId);

    List<Inventory> findByWarehouseId(UUID warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND i.quantity - i.reservedQuantity > 0")
    List<Inventory> findAvailableStock(@Param("productId") UUID productId);

    @Query("SELECT SUM(i.quantity - i.reservedQuantity) FROM Inventory i WHERE i.productId = :productId")
    Integer getTotalAvailableQuantity(@Param("productId") UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel AND i.status != 'DISCONTINUED'")
    List<Inventory> findLowStockItems();

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :qty WHERE i.id = :id")
    void addStock(@Param("id") UUID id, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.reservedQuantity = i.reservedQuantity + :qty WHERE i.id = :id")
    void reserveStock(@Param("id") UUID id, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.reservedQuantity = i.reservedQuantity - :qty, i.quantity = i.quantity - :qty WHERE i.id = :id")
    void confirmReservation(@Param("id") UUID id, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.reservedQuantity = i.reservedQuantity - :qty WHERE i.id = :id")
    void releaseReservation(@Param("id") UUID id, @Param("qty") Integer qty);
}
