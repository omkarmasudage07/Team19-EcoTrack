package com.ecotrack.pickup.service.impl;

import com.ecotrack.pickup.client.UserServiceClient;
import com.ecotrack.pickup.dto.request.SchedulePickupRequest;
import com.ecotrack.pickup.dto.response.PickupResponse;
import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.enums.PickupStatus;
import com.ecotrack.pickup.exception.BusinessException;
import com.ecotrack.pickup.producer.PickupEventProducer;
import com.ecotrack.pickup.repository.PickupImageRepository;
import com.ecotrack.pickup.repository.PickupRepository;
import com.ecotrack.pickup.repository.PickupStatusHistoryRepository;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PickupServiceImplTest {

    @Mock
    private PickupRepository pickupRepository;

    @Mock
    private PickupImageRepository pickupImageRepository;

    @Mock
    private PickupStatusHistoryRepository statusHistoryRepository;

    @Mock
    private WasteCategoryRepository wasteCategoryRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PickupEventProducer pickupEventProducer;

    @InjectMocks
    private PickupServiceImpl pickupService;

    private WasteCategory wasteCategory;
    private Pickup samplePickup;

    @BeforeEach
    void setUp() {
        wasteCategory = WasteCategory.builder()
                .id(1L)
                .name("E-Waste Electronic Items")
                .active(true)
                .build();

        samplePickup = Pickup.builder()
                .id(10L)
                .pickupNumber("PK-1001")
                .citizenId(5L)
                .pickupAddress("Flat 101, Pune Green City")
                .pickupCity("Pune")
                .regionName("Pune Region")
                .latitude(18.5204)
                .longitude(73.8567)
                .pickupDate(LocalDate.now().plusDays(1))
                .pickupTimeSlot("9AM - 11AM")
                .wasteCategory(wasteCategory)
                .status(PickupStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully schedule a pickup with region binding")
    void testSchedulePickupSuccess() {
        SchedulePickupRequest request = new SchedulePickupRequest();
        request.setPickupAddress("Flat 101, Pune Green City");
        request.setPickupCity("Pune");
        request.setPickupPincode("411001");
        request.setRegionName("Pune Region");
        request.setPickupDate(LocalDate.now().plusDays(1));
        request.setPickupTimeSlot("9AM - 11AM");
        request.setWasteCategoryId(1L);

        when(wasteCategoryRepository.findById(1L)).thenReturn(Optional.of(wasteCategory));
        when(pickupRepository.save(any(Pickup.class))).thenReturn(samplePickup);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(samplePickup));

        PickupResponse response = pickupService.schedulePickup(5L, request);

        assertNotNull(response);
        assertEquals("PK-1001", response.getPickupNumber());
        assertEquals("Pune Region", response.getRegionName());
        verify(statusHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Should filter available pickups by region and calculate distance in km")
    void testGetAvailablePickupsWithRegionAndDistance() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pickup> pickupPage = new PageImpl<>(List.of(samplePickup));

        when(pickupRepository.findByStatusAndRecyclerIdIsNullAndRegionNameIgnoreCase(
                eq(PickupStatus.PENDING), eq("Pune Region"), any(Pageable.class)
        )).thenReturn(pickupPage);

        Page<PickupResponse> result = pickupService.getAvailablePickups("Pune Region", 18.5300, 73.8400, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        PickupResponse res = result.getContent().get(0);
        assertEquals("Pune Region", res.getRegionName());
        assertNotNull(res.getDistanceKm());
        assertTrue(res.getDistanceKm() > 0);
    }

    @Test
    @DisplayName("Should accept pending pickup successfully")
    void testAcceptPickupSuccess() {
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(samplePickup));
        when(pickupRepository.save(any(Pickup.class))).thenReturn(samplePickup);

        PickupResponse response = pickupService.acceptPickup(10L, 100L);

        assertNotNull(response);
        verify(pickupEventProducer).publishPickupAccepted(any());
    }

    @Test
    @DisplayName("Should throw exception when accepting non-pending or already assigned pickup")
    void testAcceptAlreadyAssignedPickup() {
        samplePickup.setRecyclerId(99L);
        samplePickup.setStatus(PickupStatus.ACCEPTED);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(samplePickup));

        assertThrows(BusinessException.class, () -> pickupService.acceptPickup(10L, 100L));
    }
}
