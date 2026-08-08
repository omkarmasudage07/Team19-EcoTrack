package com.ecotrack.user.controller;

import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.dto.response.RecyclerResponse;
import com.ecotrack.user.security.AuthenticatedUser;
import com.ecotrack.user.service.RecyclerService;
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

@RestController
@RequestMapping("/api/v1/recyclers")
@RequiredArgsConstructor
@Tag(name = "Recyclers", description = "Recycler Partner profile management")
public class RecyclerController {

    private final RecyclerService recyclerService;

    @Operation(summary = "[Internal] Fetch a Recycler profile by userId - used by Pickup/Material services")
    @GetMapping("/internal/{userId}")
    public ResponseEntity<ApiResponse<RecyclerResponse>> getByUserIdInternal(@PathVariable Long userId) {
        RecyclerResponse response = recyclerService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recycler fetched", response));
    }

    @Operation(summary = "Get the logged in Recycler's own profile")
    @PreAuthorize("hasRole('RECYCLER')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<RecyclerResponse>> getMyProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        RecyclerResponse response = recyclerService.getByUserId(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile fetched", response));
    }

    @Operation(summary = "Get a Recycler's public company details by id - e.g. for Industry Buyers browsing the marketplace")
    @PreAuthorize("hasAnyRole('INDUSTRY','ADMIN','CITIZEN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecyclerResponse>> getById(@PathVariable Long id) {
        RecyclerResponse response = recyclerService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recycler fetched", response));
    }

    @Operation(summary = "[Admin] Search / list all Recyclers")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecyclerResponse>>> search(
            @RequestParam(required = false) String companyName, Pageable pageable) {
        Page<RecyclerResponse> response = recyclerService.search(companyName, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recyclers fetched", response));
    }

    @Operation(summary = "[Admin] Suspend or reactivate a Recycler")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<RecyclerResponse>> suspend(
            @PathVariable Long id, @RequestParam boolean suspend) {
        RecyclerResponse response = recyclerService.suspend(id, suspend);
        String message = suspend ? "Recycler suspended" : "Recycler reactivated";
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, response));
    }
}
