package com.ecotrack.user.service;

import com.ecotrack.user.dto.request.RegionRequest;
import com.ecotrack.user.dto.response.RegionResponse;

import java.util.List;

public interface RegionService {
    List<RegionResponse> getAllRegions();
    List<RegionResponse> getActiveRegions();
    RegionResponse getRegionById(Long id);
    RegionResponse createRegion(RegionRequest request);
    RegionResponse updateRegion(Long id, RegionRequest request);
    void toggleRegionStatus(Long id, boolean active);
}
