package com.ecotrack.user.controller;

import com.ecotrack.user.dto.request.RegionRequest;
import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.dto.response.RegionResponse;
import com.ecotrack.user.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/regions")
@RequiredArgsConstructor
@Tag(name = "Region Master", description = "Region management endpoints for Citizens, Recyclers, and Admins")
public class RegionController {

    private final RegionService regionService;

    @Operation(summary = "Get active regions for dropdown selection (Public/Citizen/Recycler)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getActiveRegions() {
        List<RegionResponse> regions = regionService.getActiveRegions();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Regions fetched successfully", regions));
    }

    @Operation(summary = "Get all regions including inactive ones (Admin)")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getAllRegions() {
        List<RegionResponse> regions = regionService.getAllRegions();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "All regions fetched successfully", regions));
    }

    @Operation(summary = "Get region details by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponse>> getRegionById(@PathVariable Long id) {
        RegionResponse region = regionService.getRegionById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Region fetched successfully", region));
    }

    @Operation(summary = "Create a new administrative region (Admin)")
    @PostMapping
    public ResponseEntity<ApiResponse<RegionResponse>> createRegion(@Valid @RequestBody RegionRequest request) {
        RegionResponse response = regionService.createRegion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Region created successfully", response));
    }

    @Operation(summary = "Update an existing region (Admin)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponse>> updateRegion(
            @PathVariable Long id,
            @Valid @RequestBody RegionRequest request) {
        RegionResponse response = regionService.updateRegion(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Region updated successfully", response));
    }

    @Operation(summary = "Activate or deactivate a region (Admin)")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleRegionStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        regionService.toggleRegionStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Region status updated successfully", null));
    }
}
