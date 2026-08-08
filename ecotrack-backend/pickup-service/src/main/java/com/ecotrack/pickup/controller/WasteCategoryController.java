package com.ecotrack.pickup.controller;

import com.ecotrack.pickup.dto.request.WasteCategoryRequest;
import com.ecotrack.pickup.dto.response.ApiResponse;
import com.ecotrack.pickup.dto.response.WasteCategoryResponse;
import com.ecotrack.pickup.service.WasteCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waste-categories")
@RequiredArgsConstructor
@Tag(name = "Waste Categories", description = "E-waste categories a Citizen chooses from when scheduling a pickup")
public class WasteCategoryController {

    private final WasteCategoryService wasteCategoryService;

    @Operation(summary = "List active waste categories - visible to everyone")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WasteCategoryResponse>>> getActive() {
        List<WasteCategoryResponse> response = wasteCategoryService.getAllActive();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Waste categories fetched", response));
    }

    @Operation(summary = "[Admin] List every waste category, including inactive ones")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WasteCategoryResponse>>> getAll() {
        List<WasteCategoryResponse> response = wasteCategoryService.getAll();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Waste categories fetched", response));
    }

    @Operation(summary = "[Admin] Create a new waste category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<WasteCategoryResponse>> create(@Valid @RequestBody WasteCategoryRequest request) {
        WasteCategoryResponse response = wasteCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Waste category created", response));
    }

    @Operation(summary = "[Admin] Activate or deactivate a waste category")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<WasteCategoryResponse>> setActive(
            @PathVariable Long id, @RequestParam boolean active) {
        WasteCategoryResponse response = wasteCategoryService.setActive(id, active);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Waste category updated", response));
    }
}
