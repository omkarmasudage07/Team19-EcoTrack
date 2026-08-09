package com.ecotrack.material.controller;

import com.ecotrack.material.dto.response.ApiResponse;
import com.ecotrack.material.dto.response.EcoPointTransactionResponse;
import com.ecotrack.material.dto.response.EcoPointsWalletResponse;
import com.ecotrack.material.security.AuthenticatedUser;
import com.ecotrack.material.service.EcoPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ecopoints")
@RequiredArgsConstructor
@Tag(name = "EcoPoints", description = "Citizen rewards wallet, earned for completed pickups")
public class EcoPointsController {

    private final EcoPointsService ecoPointsService;

    @Operation(summary = "[Citizen] View my EcoPoints wallet balance")
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<EcoPointsWalletResponse>> getMyWallet(@AuthenticationPrincipal AuthenticatedUser user) {
        EcoPointsWalletResponse response = ecoPointsService.getWallet(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Wallet fetched", response));
    }

    @Operation(summary = "[Citizen] View my EcoPoints transaction history")
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<EcoPointTransactionResponse>>> getMyTransactions(
            @AuthenticationPrincipal AuthenticatedUser user, Pageable pageable) {
        Page<EcoPointTransactionResponse> response = ecoPointsService.getTransactions(user.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transactions fetched", response));
    }
}
