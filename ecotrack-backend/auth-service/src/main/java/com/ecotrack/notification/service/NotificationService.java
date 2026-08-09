package com.ecotrack.notification.service;

import com.ecotrack.notification.dto.response.AuditLogResponse;
import com.ecotrack.notification.dto.response.NotificationResponse;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /** Creates a notification for one user. Called by the RabbitMQ consumers. */
    void notify(Long userId, RoleType userRole, String title, String message, NotificationType type);

    Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    /** Simple audit trail, populated by the same consumers. */
    void logAudit(String action, String details);

    Page<AuditLogResponse> getAuditLogs(Pageable pageable);

    Page<AuditLogResponse> getFilteredAuditLogs(String role, String region, Integer status, String search, Pageable pageable);
}
