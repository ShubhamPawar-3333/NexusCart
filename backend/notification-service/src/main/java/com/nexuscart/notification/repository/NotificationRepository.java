package com.nexuscart.notification.repository;

import com.nexuscart.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    List<Notification> findByReferenceId(String referenceId);
}
