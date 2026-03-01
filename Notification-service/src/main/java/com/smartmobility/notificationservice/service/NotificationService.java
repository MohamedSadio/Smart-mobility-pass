package com.smartmobility.notificationservice.service;

import com.smartmobility.notificationservice.dto.NotificationDto;
import com.smartmobility.notificationservice.entity.Notification;
import com.smartmobility.notificationservice.event.NotificationEvent;
import com.smartmobility.notificationservice.mapper.NotificationMapper;
import com.smartmobility.notificationservice.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper     notificationMapper;

    @Transactional
    public void createFromEvent(NotificationEvent event) {
        Notification notification = notificationMapper.toEntity(event);
        notificationRepository.save(notification);
        log.info("[NOTIFICATION] Créée — type={}, userId={}", notification.getType(), notification.getUserId());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getAllByUserId(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getUnreadByUserId(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationDto.NotificationResponse markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification introuvable : " + notificationId));
        notification.setRead(true);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("[NOTIFICATION] Tout marqué comme lu — userId={}", userId);
    }
}