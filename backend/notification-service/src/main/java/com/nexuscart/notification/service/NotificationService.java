package com.nexuscart.notification.service;

import com.nexuscart.notification.entity.Notification;
import com.nexuscart.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public Notification createAndSendNotification(UUID userId, String recipient,
            Notification.NotificationType type, Notification.NotificationChannel channel,
            String subject, String content, String referenceId) {

        Notification notification = Notification.builder()
                .userId(userId)
                .recipient(recipient)
                .type(type)
                .channel(channel)
                .subject(subject)
                .content(content)
                .referenceId(referenceId)
                .status(Notification.NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        // Send based on channel
        try {
            switch (channel) {
                case EMAIL -> sendEmail(recipient, subject, content);
                case SMS -> sendSms(recipient, content);
                case PUSH -> sendPush(userId, subject, content);
            }

            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Sent {} notification to {}", type, recipient);

        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification: {}", e.getMessage());
        }

        return notificationRepository.save(notification);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@nexuscart.com");

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.warn("Email sending disabled or failed: {}", e.getMessage());
            // Don't fail - just log (for development without SMTP)
        }
    }

    private void sendSms(String phoneNumber, String content) {
        // Mock SMS sending - integrate with Twilio/AWS SNS in production
        log.info("SMS would be sent to {}: {}", phoneNumber, content);
    }

    private void sendPush(UUID userId, String title, String body) {
        // Mock push notification - integrate with Firebase in production
        log.info("Push notification would be sent to user {}: {}", userId, title);
    }
}
