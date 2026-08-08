package com.ecotrack.user.controller;

import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.dto.response.IndustryResponse;
import com.ecotrack.user.security.AuthenticatedUser;
import com.ecotrack.user.service.IndustryService;
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
@RequestMapping("/api/v1/industries")
@RequiredArgsConstructor
@Tag(name = "Industries", description = "Industrial Buyer profile management")
public class IndustryController {

    private final IndustryService industryService;

    @Operation(summary = "[Internal] Fetch an Industry profile by userId - used by Material service")
    @GetMapping("/internal/{userId}")
    public ResponseEntity<ApiResponse<IndustryResponse>> getByUserIdInternal(@PathVariable Long userId) {
        IndustryResponse response = industryService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Industry fetched", response));
    }

    @Operation(summary = "Get the logged in Industry's own profile")
    @PreAuthorize("hasRole('INDUSTRY')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<IndustryResponse>> getMyProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        IndustryResponse response = industryService.getByUserId(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile fetched", response));
    }

    @Operation(summary = "Get an Industry's public company details by id - e.g. for a Recycler viewing an order")
    @PreAuthorize("hasAnyRole('RECYCLER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IndustryResponse>> getById(@PathVariable Long id) {
        IndustryResponse response = industryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Industry fetched", response));
    }

    @Operation(summary = "[Admin] Search / list all Industries")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<IndustryResponse>>> search(
            @RequestParam(required = false) String companyName, Pageable pageable) {
        Page<IndustryResponse> response = industryService.search(companyName, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Industries fetched", response));
    }

    @Operation(summary = "[Admin] Suspend or reactivate an Industry")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<IndustryResponse>> suspend(
            @PathVariable Long id, @RequestParam boolean suspend) {
        IndustryResponse response = industryService.suspend(id, suspend);
        String message = suspend ? "Industry suspended" : "Industry reactivated";
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, response));
    }
}
