package com.ecotrack.material.controller;

import com.ecotrack.material.dto.request.MaterialCategoryRequest;
import com.ecotrack.material.dto.response.ApiResponse;
import com.ecotrack.material.dto.response.MaterialCategoryResponse;
import com.ecotrack.material.service.MaterialCategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Material Categories", description = "Recovered-material categories (Copper, Aluminium, Plastic...)")
public class MaterialCategoryController {

    private final MaterialCategoryService categoryService;

    @Operation(summary = "List active material categories - visible to everyone")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialCategoryResponse>>> getActive() {
        List<MaterialCategoryResponse> response = categoryService.getAllActive();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories fetched", response));
    }

    @Operation(summary = "[Admin] List every material category, including inactive ones")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MaterialCategoryResponse>>> getAll() {
        List<MaterialCategoryResponse> response = categoryService.getAll();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories fetched", response));
    }

    @Operation(summary = "[Admin] Create a new material category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MaterialCategoryResponse>> create(@Valid @RequestBody MaterialCategoryRequest request) {
        MaterialCategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created", response));
    }

    @Operation(summary = "[Admin] Activate or deactivate a material category")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<MaterialCategoryResponse>> setActive(
            @PathVariable Long id, @RequestParam boolean active) {
        MaterialCategoryResponse response = categoryService.setActive(id, active);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category updated", response));
    }
}
