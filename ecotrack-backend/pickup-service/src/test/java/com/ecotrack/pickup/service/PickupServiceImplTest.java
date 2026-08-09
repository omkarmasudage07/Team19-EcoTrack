package com.ecotrack.pickup.service;

import com.ecotrack.pickup.client.UserServiceClient;
import com.ecotrack.pickup.dto.request.UpdatePickupStatusRequest;
import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.enums.PickupStatus;
import com.ecotrack.pickup.exception.BusinessException;
import com.ecotrack.pickup.producer.PickupEventProducer;
import com.ecotrack.pickup.repository.PickupImageRepository;
import com.ecotrack.pickup.repository.PickupRepository;
import com.ecotrack.pickup.repository.PickupStatusHistoryRepository;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import com.ecotrack.pickup.service.impl.PickupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private Pickup samplePickup(PickupStatus status, Long recyclerId) {
        WasteCategory category = WasteCategory.builder().id(1L).name("Mobile Phones").active(true).build();
        return Pickup.builder()
                .id(10L)
                .pickupNumber("PKP-20260803-1234")
                .citizenId(100L)
                .recyclerId(recyclerId)
                .pickupAddress("123 Main St")
                .pickupDate(LocalDate.now().plusDays(1))
                .pickupTimeSlot("10AM-12PM")
                .wasteCategory(category)
                .status(status)
                .build();
    }

    @Test
    void updateStatus_rejectsSkippingAStage() {
        Pickup pickup = samplePickup(PickupStatus.ACCEPTED, 200L);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));

        // Trying to jump straight from ACCEPTED to COMPLETED, skipping
        // ON_THE_WAY, COLLECTED and PROCESSING, must be rejected.
        UpdatePickupStatusRequest request = new UpdatePickupStatusRequest(PickupStatus.COMPLETED, null);

        assertThatThrownBy(() -> pickupService.updateStatus(10L, 200L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void updateStatus_rejectsWhenCallerIsNotTheAssignedRecycler() {
        Pickup pickup = samplePickup(PickupStatus.ACCEPTED, 200L);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));

        UpdatePickupStatusRequest request = new UpdatePickupStatusRequest(PickupStatus.ON_THE_WAY, null);

        assertThatThrownBy(() -> pickupService.updateStatus(10L, 999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not assigned");
    }

    @Test
    void updateStatus_allowsTheCorrectNextStage() {
        Pickup pickup = samplePickup(PickupStatus.ACCEPTED, 200L);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));
        when(pickupRepository.save(any(Pickup.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pickupImageRepository.findByPickupId(10L)).thenReturn(Collections.emptyList());
        when(statusHistoryRepository.findByPickupIdOrderByUpdatedAtAsc(10L)).thenReturn(Collections.emptyList());

        UpdatePickupStatusRequest request = new UpdatePickupStatusRequest(PickupStatus.ON_THE_WAY, null);

        var result = pickupService.updateStatus(10L, 200L, request);

        assertThat(result.getStatus()).isEqualTo(PickupStatus.ON_THE_WAY);
    }

    @Test
    void acceptPickup_rejectsWhenAlreadyAcceptedByAnotherRecycler() {
        Pickup pickup = samplePickup(PickupStatus.ACCEPTED, 200L);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));

        assertThatThrownBy(() -> pickupService.acceptPickup(10L, 300L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already been accepted");
    }

    @Test
    void cancelPickup_rejectsOnceRecyclerHasAccepted() {
        Pickup pickup = samplePickup(PickupStatus.ACCEPTED, 200L);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));

        assertThatThrownBy(() -> pickupService.cancelPickup(10L, 100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("can no longer be cancelled");
    }

    @Test
    void cancelPickup_rejectsWhenNotTheOwningCitizen() {
        Pickup pickup = samplePickup(PickupStatus.PENDING, null);
        when(pickupRepository.findById(10L)).thenReturn(Optional.of(pickup));

        assertThatThrownBy(() -> pickupService.cancelPickup(10L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("your own pickups");
    }
}
