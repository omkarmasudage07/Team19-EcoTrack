package com.ecotrack.user.controller;

import com.ecotrack.user.dto.request.CitizenProfileUpdateRequest;
import com.ecotrack.user.dto.request.CreateCitizenProfileRequest;
import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.dto.response.CitizenResponse;
import com.ecotrack.user.security.AuthenticatedUser;
import com.ecotrack.user.service.CitizenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/citizens")
@RequiredArgsConstructor
@Tag(name = "Citizens", description = "Citizen profile management")
public class CitizenController {

    private final CitizenService citizenService;

    @Operation(summary = "[Internal] Create a Citizen profile right after registration - called by Auth Service")
    @PostMapping("/internal")
    public ResponseEntity<ApiResponse<CitizenResponse>> createProfile(@Valid @RequestBody CreateCitizenProfileRequest request) {
        CitizenResponse response = citizenService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Citizen profile created", response));
    }

    @Operation(summary = "[Internal] Fetch a Citizen profile by userId - used by Pickup/Material services")
    @GetMapping("/internal/{userId}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getByUserIdInternal(@PathVariable Long userId) {
        CitizenResponse response = citizenService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Citizen fetched", response));
    }

    @Operation(summary = "Get the logged in Citizen's own profile")
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CitizenResponse>> getMyProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        CitizenResponse response = citizenService.getByUserId(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile fetched", response));
    }

    @Operation(summary = "Update the logged in Citizen's own profile")
    @PreAuthorize("hasRole('CITIZEN')")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CitizenResponse>> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CitizenProfileUpdateRequest request) {
        CitizenResponse response = citizenService.updateProfile(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile updated successfully", response));
    }

    @Operation(summary = "Update the logged in Citizen's profile photo")
    @PreAuthorize("hasRole('CITIZEN')")
    @PatchMapping("/me/photo")
    public ResponseEntity<ApiResponse<CitizenResponse>> updateMyPhoto(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam String photoUrl) {
        CitizenResponse response = citizenService.updateProfilePhoto(user.getUserId(), photoUrl);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile photo updated", response));
    }
}
