package com.ecotrack.notification.mapper;

import com.ecotrack.notification.dto.response.AuditLogResponse;
import com.ecotrack.notification.dto.response.NotificationResponse;
import com.ecotrack.notification.entity.AuditLog;
import com.ecotrack.notification.entity.Notification;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .requestId(auditLog.getRequestId())
                .userId(auditLog.getUserId())
                .userRole(auditLog.getUserRole())
                .region(auditLog.getRegion())
                .method(auditLog.getMethod())
                .url(auditLog.getUrl())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .responseStatus(auditLog.getResponseStatus())
                .responseTimeMs(auditLog.getResponseTimeMs())
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
