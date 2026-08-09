package com.ecotrack.material.controller;

import com.ecotrack.material.dto.request.MaterialRequest;
import com.ecotrack.material.dto.response.ApiResponse;
import com.ecotrack.material.dto.response.MaterialResponse;
import com.ecotrack.material.security.AuthenticatedUser;
import com.ecotrack.material.service.MaterialService;
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
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materials", description = "Recycler material listings and the Industry marketplace")
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "[Recycler] List a new recovered material")
    @PreAuthorize("hasRole('RECYCLER')")
    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody MaterialRequest request) {
        MaterialResponse response = materialService.createMaterial(user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Material listed successfully", response));
    }

    @Operation(summary = "[Recycler] Update one of my own material listings")
    @PreAuthorize("hasRole('RECYCLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequest request) {
        MaterialResponse response = materialService.updateMaterial(id, user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Material updated", response));
    }

    @Operation(summary = "[Recycler] Delete one of my own material listings")
    @PreAuthorize("hasRole('RECYCLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        materialService.deleteMaterial(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Material deleted", null));
    }

    @Operation(summary = "[Recycler] View my own material inventory")
    @PreAuthorize("hasRole('RECYCLER')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<MaterialResponse>>> getMyMaterials(
            @AuthenticationPrincipal AuthenticatedUser user, Pageable pageable) {
        Page<MaterialResponse> response = materialService.getRecyclerMaterials(user.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Materials fetched", response));
    }

    @Operation(summary = "Browse the marketplace - search by name or filter by category")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MaterialResponse>>> browseMarketplace(
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        Page<MaterialResponse> response = materialService.browseMarketplace(materialName, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Materials fetched", response));
    }

    @Operation(summary = "View full details of a single material listing")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> getDetail(@PathVariable Long id) {
        MaterialResponse response = materialService.getMaterialDetail(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Material fetched", response));
    }
}
