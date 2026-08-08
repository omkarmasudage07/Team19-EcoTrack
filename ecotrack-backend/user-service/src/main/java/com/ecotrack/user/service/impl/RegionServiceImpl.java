package com.ecotrack.user.service.impl;

import com.ecotrack.user.dto.request.RegionRequest;
import com.ecotrack.user.dto.response.RegionResponse;
import com.ecotrack.user.entity.Region;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.repository.RegionRepository;
import com.ecotrack.user.service.RegionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @PostConstruct
    public void seedDefaultRegions() {
        List<String> defaultRegions = Arrays.asList(
                "Pune Region", "Mumbai Region", "Kolhapur Region",
                "Nagpur Region", "Nashik Region", "Satara Region"
        );

        for (String name : defaultRegions) {
            if (!regionRepository.existsByNameIgnoreCase(name)) {
                String code = name.replaceAll("[^A-Za-z]", "").toUpperCase();
                if (code.length() > 6) code = code.substring(0, 6);
                Region region = Region.builder()
                        .name(name)
                        .code(code)
                        .description("Default administrative region: " + name)
                        .active(true)
                        .build();
                regionRepository.save(region);
                log.info("Seeded default region: {}", name);
            }
        }
    }

    @Override
    public List<RegionResponse> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<RegionResponse> getActiveRegions() {
        return regionRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RegionResponse getRegionById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));
        return toResponse(region);
    }

    @Override
    @Transactional
    public RegionResponse createRegion(RegionRequest request) {
        if (regionRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BusinessException("A region with this name already exists", HttpStatus.CONFLICT);
        }

        Region region = Region.builder()
                .name(request.getName().trim())
                .code(request.getCode() != null ? request.getCode().trim().toUpperCase() : null)
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();

        region = regionRepository.save(region);
        log.info("Created new region: {}", region.getName());
        return toResponse(region);
    }

    @Override
    @Transactional
    public RegionResponse updateRegion(Long id, RegionRequest request) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));

        String newName = request.getName().trim();
        if (!region.getName().equalsIgnoreCase(newName) && regionRepository.existsByNameIgnoreCase(newName)) {
            throw new BusinessException("A region with this name already exists", HttpStatus.CONFLICT);
        }

        region.setName(newName);
        if (request.getCode() != null) region.setCode(request.getCode().trim().toUpperCase());
        if (request.getDescription() != null) region.setDescription(request.getDescription());
        if (request.getActive() != null) region.setActive(request.getActive());

        region = regionRepository.save(region);
        log.info("Updated region {}: {}", id, region.getName());
        return toResponse(region);
    }

    @Override
    @Transactional
    public void toggleRegionStatus(Long id, boolean active) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));
        region.setActive(active);
        regionRepository.save(region);
        log.info("Toggled region {} active state to: {}", id, active);
    }

    private RegionResponse toResponse(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .code(region.getCode())
                .description(region.getDescription())
                .active(region.isActive())
                .createdAt(region.getCreatedAt())
                .build();
    }
}
