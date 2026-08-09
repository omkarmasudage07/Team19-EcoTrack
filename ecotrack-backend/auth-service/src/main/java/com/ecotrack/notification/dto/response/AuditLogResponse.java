package com.ecotrack.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String requestId;
    private Long userId;
    private String userRole;
    private String region;
    private String method;
    private String url;
    private String ipAddress;
    private String userAgent;
    private Integer responseStatus;
    private Long responseTimeMs;
    private String action;
    private String details;
    private LocalDateTime createdAt;
}
