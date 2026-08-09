package com.ecotrack.notification.controller;

import com.ecotrack.notification.dto.response.ApiResponse;
import com.ecotrack.notification.dto.response.AuditLogResponse;
import com.ecotrack.notification.dto.response.NotificationResponse;
import com.ecotrack.notification.security.AuthenticatedUser;
import com.ecotrack.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications for every role")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "View my notifications, most recent first")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal AuthenticatedUser user, Pageable pageable) {
        if (user == null || user.getUserId() == null) {
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notifications fetched", Page.empty(pageable)));
        }
        Page<NotificationResponse> response = notificationService.getMyNotifications(user.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notifications fetched", response));
    }

    @Operation(summary = "Get my unread notification count - for the navbar bell badge")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null || user.getUserId() == null) {
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Unread count fetched", Map.of("unreadCount", 0L)));
        }
        long count = notificationService.getUnreadCount(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Unread count fetched", Map.of("unreadCount", count)));
    }

    @Operation(summary = "Mark a single notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        if (user != null && user.getUserId() != null) {
            notificationService.markAsRead(id, user.getUserId());
        }
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notification marked as read", null));
    }

    @Operation(summary = "Mark every one of my notifications as read")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user != null && user.getUserId() != null) {
            notificationService.markAllAsRead(user.getUserId());
        }
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "All notifications marked as read", null));
    }

    @Operation(summary = "[Admin] View the platform audit log")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<AuditLogResponse> response = notificationService.getFilteredAuditLogs(role, region, status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Audit logs fetched", response));
    }
}
