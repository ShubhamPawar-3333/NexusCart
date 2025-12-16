package com.nexuscart.inventory.repository;

import com.nexuscart.inventory.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    List<StockReservation> findByOrderId(String orderId);

    List<StockReservation> findByOrderIdAndStatus(String orderId, StockReservation.ReservationStatus status);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = 'PENDING' AND sr.expiresAt < :now")
    List<StockReservation> findExpiredReservations(LocalDateTime now);

    @Modifying
    @Query("UPDATE StockReservation sr SET sr.status = 'EXPIRED' WHERE sr.status = 'PENDING' AND sr.expiresAt < :now")
    int expireOldReservations(LocalDateTime now);
}
