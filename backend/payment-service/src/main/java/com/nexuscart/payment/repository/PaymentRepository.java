package com.nexuscart.payment.repository;

import com.nexuscart.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByUserId(UUID userId);

    Optional<Payment> findByTransactionId(String transactionId);
}
