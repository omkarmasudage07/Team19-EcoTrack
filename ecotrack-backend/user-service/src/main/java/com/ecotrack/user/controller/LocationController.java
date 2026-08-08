package com.ecotrack.user.controller;

import com.ecotrack.user.dto.response.ApiResponse;
import com.ecotrack.user.service.StateCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/locations")
@RequiredArgsConstructor
public class LocationController {

    private final StateCityService stateCityService;

    @GetMapping("/states")
    public ResponseEntity<ApiResponse<List<String>>> getAllStates() {
        return ResponseEntity.ok(ApiResponse.success(200, "States retrieved successfully", stateCityService.getAllStates()));
    }

    @GetMapping("/states/{stateName}/cities")
    public ResponseEntity<ApiResponse<List<String>>> getCitiesByState(@PathVariable String stateName) {
        return ResponseEntity.ok(ApiResponse.success(200, "Cities retrieved successfully", stateCityService.getCitiesByState(stateName)));
    }
}
