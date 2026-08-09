package com.ecotrack.material.controller;

import com.ecotrack.material.dto.request.EcoPointRuleRequest;
import com.ecotrack.material.dto.response.ApiResponse;
import com.ecotrack.material.dto.response.EcoPointRuleResponse;
import com.ecotrack.material.service.EcoPointRuleService;
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
@RequestMapping("/api/v1/ecopoint-rules")
@RequiredArgsConstructor
@Tag(name = "EcoPoint Rules", description = "Admin rules for EcoPoints per waste category")
public class EcoPointRuleController {

    private final EcoPointRuleService ecoPointRuleService;

    @Operation(summary = "Get all EcoPoint rules")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EcoPointRuleResponse>>> getAllRules() {
        List<EcoPointRuleResponse> rules = ecoPointRuleService.getAllRules();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "EcoPoint rules fetched", rules));
    }

    @Operation(summary = "Get EcoPoint rule by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EcoPointRuleResponse>> getRuleById(@PathVariable Long id) {
        EcoPointRuleResponse rule = ecoPointRuleService.getRuleById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "EcoPoint rule fetched", rule));
    }

    @Operation(summary = "[Admin] Create new EcoPoint rule")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<EcoPointRuleResponse>> createRule(@Valid @RequestBody EcoPointRuleRequest request) {
        EcoPointRuleResponse response = ecoPointRuleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "EcoPoint rule created successfully", response));
    }

    @Operation(summary = "[Admin] Update EcoPoint rule")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EcoPointRuleResponse>> updateRule(
            @PathVariable Long id, @Valid @RequestBody EcoPointRuleRequest request) {
        EcoPointRuleResponse response = ecoPointRuleService.updateRule(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "EcoPoint rule updated successfully", response));
    }

    @Operation(summary = "[Admin] Delete EcoPoint rule")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        ecoPointRuleService.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "EcoPoint rule deleted successfully", null));
    }
}
