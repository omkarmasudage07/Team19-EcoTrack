package com.ecotrack.user.service.impl;

import com.ecotrack.user.dto.request.RegionRequest;
import com.ecotrack.user.dto.response.RegionResponse;
import com.ecotrack.user.entity.Region;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    private Region sampleRegion;

    @BeforeEach
    void setUp() {
        sampleRegion = Region.builder()
                .id(1L)
                .name("Pune Region")
                .code("PUNE")
                .description("Pune Division")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should fetch all active regions")
    void testGetActiveRegions() {
        when(regionRepository.findByActiveTrue()).thenReturn(List.of(sampleRegion));

        List<RegionResponse> result = regionService.getActiveRegions();

        assertEquals(1, result.size());
        assertEquals("Pune Region", result.get(0).getName());
    }

    @Test
    @DisplayName("Should create a new region successfully")
    void testCreateRegionSuccess() {
        RegionRequest request = new RegionRequest("Mumbai Region", "MUMBAI", "Mumbai Metro", true);
        when(regionRepository.existsByNameIgnoreCase("Mumbai Region")).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenReturn(
                Region.builder().id(2L).name("Mumbai Region").code("MUMBAI").active(true).build()
        );

        RegionResponse response = regionService.createRegion(request);

        assertNotNull(response);
        assertEquals("Mumbai Region", response.getName());
    }

    @Test
    @DisplayName("Should throw BusinessException when creating a duplicate region")
    void testCreateDuplicateRegion() {
        RegionRequest request = new RegionRequest("Pune Region", "PUNE", "Desc", true);
        when(regionRepository.existsByNameIgnoreCase("Pune Region")).thenReturn(true);

        assertThrows(BusinessException.class, () -> regionService.createRegion(request));
    }

    @Test
    @DisplayName("Should toggle region active status")
    void testToggleRegionStatus() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(sampleRegion));

        regionService.toggleRegionStatus(1L, false);

        assertFalse(sampleRegion.isActive());
        verify(regionRepository).save(sampleRegion);
    }
}
