package com.ecotrack.user.controller;

import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.IndustryApplicationRequest;
import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.dto.response.IndustryApplicationResponse;
import com.ecotrack.user.enums.ApprovalStatus;
import com.ecotrack.user.security.AuthenticatedUser;
import com.ecotrack.user.service.IndustryApplicationService;
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
@RequestMapping("/api/v1/industry-applications")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequiredArgsConstructor
@Tag(name = "Industry Applications", description = "Become an Industrial Buyer - company verification & Admin review workflow")
public class IndustryApplicationController {

    private final IndustryApplicationService applicationService;

    @Operation(summary = "Submit an Industry verification application - public, no login required")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<IndustryApplicationResponse>> apply(
            @Valid @RequestBody IndustryApplicationRequest request) {
        IndustryApplicationResponse response = applicationService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(),
                        "Application submitted successfully. You will be notified once it is reviewed.", response));
    }

    @Operation(summary = "[Admin] List Industry applications, optionally filtered by status")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<IndustryApplicationResponse>>> getApplications(
            @RequestParam(required = false) ApprovalStatus status, Pageable pageable) {
        Page<IndustryApplicationResponse> response = applicationService.getApplications(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Applications fetched", response));
    }

    @Operation(summary = "[Admin] Approve or reject an Industry application")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/review")
    public ResponseEntity<ApiResponse<IndustryApplicationResponse>> review(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser admin,
            @Valid @RequestBody ApplicationReviewRequest request) {
        Long adminUserId = admin != null ? admin.getUserId() : null;
        IndustryApplicationResponse response = applicationService.reviewApplication(id, adminUserId, request);
        String message = Boolean.TRUE.equals(request.getApprove())
                ? "Application approved. Login credentials have been created."
                : "Application rejected.";
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, response));
    }
}
