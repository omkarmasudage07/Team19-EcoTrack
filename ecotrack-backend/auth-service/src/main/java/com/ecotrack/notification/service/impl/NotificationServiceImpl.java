package com.ecotrack.notification.service.impl;

import com.ecotrack.notification.dto.response.AuditLogResponse;
import com.ecotrack.notification.dto.response.NotificationResponse;
import com.ecotrack.notification.entity.AuditLog;
import com.ecotrack.notification.entity.Notification;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.exception.BusinessException;
import com.ecotrack.notification.exception.ResourceNotFoundException;
import com.ecotrack.notification.mapper.NotificationMapper;
import com.ecotrack.notification.repository.AuditLogRepository;
import com.ecotrack.notification.repository.NotificationRepository;
import com.ecotrack.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void notify(Long userId, RoleType userRole, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userRole(userRole)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .build();
        notificationRepository.save(notification);
        log.info("Notification created for user {} ({}): {}", userId, userRole, title);
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("You can only manage your own notifications", HttpStatus.FORBIDDEN);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void logAudit(String action, String details) {
        auditLogRepository.save(AuditLog.builder().action(action).details(details).build());
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getFilteredAuditLogs(String role, String region, Integer status, String search, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        String cleanRole = (role != null && !role.isBlank() && !role.equalsIgnoreCase("ALL")) ? role.trim() : null;
        String cleanRegion = (region != null && !region.isBlank() && !region.equalsIgnoreCase("ALL")) ? region.trim() : null;
        return auditLogRepository.searchAuditLogs(cleanRole, cleanRegion, status, cleanSearch, pageable)
                .map(NotificationMapper::toResponse);
    }
}
