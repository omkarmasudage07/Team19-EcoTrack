package com.ecotrack.pickup.controller;

import com.ecotrack.pickup.dto.request.SchedulePickupRequest;
import com.ecotrack.pickup.dto.request.UpdatePickupStatusRequest;
import com.ecotrack.pickup.dto.response.ApiResponse;
import com.ecotrack.pickup.dto.response.PickupResponse;
import com.ecotrack.pickup.enums.PickupStatus;
import com.ecotrack.pickup.security.AuthenticatedUser;
import com.ecotrack.pickup.service.PickupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pickups")
@RequiredArgsConstructor
@Tag(name = "Pickups", description = "Schedule, assign, track and complete e-waste pickups")
public class PickupController {

    private final PickupService pickupService;

    // ------------------------------------------------------------------
    // Citizen
    // ------------------------------------------------------------------

    @Operation(summary = "[Citizen] Schedule a new pickup")
    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PickupResponse>> schedulePickup(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody SchedulePickupRequest request) {
        PickupResponse response = pickupService.schedulePickup(user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Pickup scheduled successfully", response));
    }

    @Operation(summary = "[Citizen] View my own pickup history, optionally filtered by status")
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PickupResponse>>> getMyPickups(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) PickupStatus status,
            Pageable pageable) {
        Page<PickupResponse> response = pickupService.getCitizenPickups(user.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickups fetched", response));
    }

    @Operation(summary = "[Citizen] Cancel a pickup - only allowed while it is still PENDING")
    @PreAuthorize("hasRole('CITIZEN')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PickupResponse>> cancelPickup(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        PickupResponse response = pickupService.cancelPickup(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickup cancelled", response));
    }

    // ------------------------------------------------------------------
    // Recycler
    // ------------------------------------------------------------------

    @Operation(summary = "[Recycler] View pickups not yet accepted by any Recycler in region, sorted by distance")
    @PreAuthorize("hasRole('RECYCLER')")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<PickupResponse>>> getAvailablePickups(
            @RequestParam(required = false) String regionName,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            Pageable pageable) {
        Page<PickupResponse> response = pickupService.getAvailablePickups(regionName, lat, lng, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Available pickups fetched", response));
    }

    @Operation(summary = "[Recycler] View pickups assigned to me, optionally filtered by status")
    @PreAuthorize("hasRole('RECYCLER')")
    @GetMapping("/assigned")
    public ResponseEntity<ApiResponse<Page<PickupResponse>>> getAssignedPickups(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) PickupStatus status,
            Pageable pageable) {
        Page<PickupResponse> response = pickupService.getRecyclerPickups(user.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Assigned pickups fetched", response));
    }

    @Operation(summary = "[Recycler] Accept an available pickup")
    @PreAuthorize("hasRole('RECYCLER')")
    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<PickupResponse>> acceptPickup(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        PickupResponse response = pickupService.acceptPickup(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickup accepted", response));
    }

    @Operation(summary = "[Recycler] Decline an available pickup - it stays visible to other Recyclers")
    @PreAuthorize("hasRole('RECYCLER')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<PickupResponse>> rejectPickup(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        PickupResponse response = pickupService.rejectPickup(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickup declined", response));
    }

    @Operation(summary = "[Recycler] Move an assigned pickup to its next status (ON_THE_WAY -> COLLECTED -> PROCESSING -> COMPLETED)")
    @PreAuthorize("hasRole('RECYCLER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PickupResponse>> updateStatus(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePickupStatusRequest request) {
        PickupResponse response = pickupService.updateStatus(id, user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickup status updated", response));
    }

    // ------------------------------------------------------------------
    // Shared (Citizen who owns it / Recycler assigned to it / Admin) & Admin
    // ------------------------------------------------------------------

    @Operation(summary = "View full details of a single pickup, including its image and status history")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PickupResponse>> getPickupDetail(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        PickupResponse response = pickupService.getPickupDetail(id);

        boolean isOwner = "CITIZEN".equals(user.getRole()) && response.getCitizenId().equals(user.getUserId());
        boolean isAssignedRecycler = "RECYCLER".equals(user.getRole())
                && response.getRecyclerId() != null && response.getRecyclerId().equals(user.getUserId());
        boolean isAdmin = "ADMIN".equals(user.getRole());

        if (!isOwner && !isAssignedRecycler && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "You do not have access to this pickup", null));
        }

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickup fetched", response));
    }

    @Operation(summary = "[Admin] View every pickup in the system, optionally filtered by status")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PickupResponse>>> getAllPickups(
            @RequestParam(required = false) PickupStatus status, Pageable pageable) {
        Page<PickupResponse> response = pickupService.getAllPickups(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pickups fetched", response));
    }
}
