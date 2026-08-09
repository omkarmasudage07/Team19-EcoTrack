package com.ecotrack.material.controller;

import com.ecotrack.material.dto.request.CreateRewardRequest;
import com.ecotrack.material.dto.request.RedeemRewardRequest;
import com.ecotrack.material.dto.request.UpdateRewardRequest;
import com.ecotrack.material.dto.response.*;
import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.enums.RewardOrderStatus;
import com.ecotrack.material.security.AuthenticatedUser;
import com.ecotrack.material.service.RewardService;
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
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Rewards Marketplace", description = "Citizen rewards catalog, point redemptions, and Admin rewards management")
public class RewardController {

    private final RewardService rewardService;

    @Operation(summary = "Browse rewards catalog")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RewardResponse>>> getRewards(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) RewardCategory category,
            @RequestParam(defaultValue = "false") boolean inStockOnly,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<RewardResponse> response = rewardService.getRewards(active, category, inStockOnly, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Rewards catalog fetched", response));
    }

    @Operation(summary = "Get reward item detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> getRewardById(@PathVariable Long id) {
        RewardResponse response = rewardService.getRewardById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward item fetched", response));
    }

    @Operation(summary = "[Admin] Create new reward item")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<RewardResponse>> createReward(@Valid @RequestBody CreateRewardRequest request) {
        RewardResponse response = rewardService.createReward(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Reward created successfully", response));
    }

    @Operation(summary = "[Admin] Update reward item")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> updateReward(
            @PathVariable Long id, @Valid @RequestBody UpdateRewardRequest request) {
        RewardResponse response = rewardService.updateReward(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward updated successfully", response));
    }

    @Operation(summary = "[Admin] Delete reward item")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward deleted successfully", null));
    }

    @Operation(summary = "[Admin] Toggle active/inactive status")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<RewardResponse>> toggleActive(@PathVariable Long id) {
        RewardResponse response = rewardService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward status updated", response));
    }

    @Operation(summary = "[Citizen] Redeem reward using EcoPoints")
    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping("/{id}/redeem")
    public ResponseEntity<ApiResponse<RewardOrderResponse>> redeemReward(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @RequestBody(required = false) RedeemRewardRequest request) {
        RewardOrderResponse response = rewardService.redeemReward(user.getUserId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Reward redeemed successfully!", response));
    }

    @Operation(summary = "[Citizen] View my reward redemption order history")
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<RewardOrderResponse>>> getMyOrders(
            @AuthenticationPrincipal AuthenticatedUser user, Pageable pageable) {
        Page<RewardOrderResponse> response = rewardService.getCitizenOrders(user.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "My reward orders fetched", response));
    }

    @Operation(summary = "[Admin] View all citizen reward orders")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders")
    public ResponseEntity<ApiResponse<Page<RewardOrderResponse>>> getAdminOrders(
            @RequestParam(required = false) RewardOrderStatus status, Pageable pageable) {
        Page<RewardOrderResponse> response = rewardService.getAdminOrders(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Admin reward orders fetched", response));
    }

    @Operation(summary = "[Admin] Update reward order status")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<ApiResponse<RewardOrderResponse>> updateOrderStatus(
            @PathVariable Long id, @RequestParam RewardOrderStatus status) {
        RewardOrderResponse response = rewardService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward order status updated", response));
    }

    @Operation(summary = "[Admin] Get EcoPoints & Reward analytics reports")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports")
    public ResponseEntity<ApiResponse<RewardReportResponse>> getAdminReports() {
        RewardReportResponse response = rewardService.getAdminReportStats();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reward report stats fetched", response));
    }
}
