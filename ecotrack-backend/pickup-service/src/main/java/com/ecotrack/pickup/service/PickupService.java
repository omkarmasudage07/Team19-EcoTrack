package com.ecotrack.pickup.service;

import com.ecotrack.pickup.dto.request.SchedulePickupRequest;
import com.ecotrack.pickup.dto.request.UpdatePickupStatusRequest;
import com.ecotrack.pickup.dto.response.PickupResponse;
import com.ecotrack.pickup.enums.PickupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PickupService {

    PickupResponse schedulePickup(Long citizenId, SchedulePickupRequest request);

    PickupResponse getPickupDetail(Long pickupId);

    Page<PickupResponse> getCitizenPickups(Long citizenId, PickupStatus status, Pageable pageable);

    Page<PickupResponse> getAvailablePickups(String regionName, Double recyclerLat, Double recyclerLng, Pageable pageable);

    Page<PickupResponse> getRecyclerPickups(Long recyclerId, PickupStatus status, Pageable pageable);

    Page<PickupResponse> getAllPickups(PickupStatus status, Pageable pageable);

    PickupResponse acceptPickup(Long pickupId, Long recyclerId);

    PickupResponse rejectPickup(Long pickupId, Long recyclerId);

    PickupResponse updateStatus(Long pickupId, Long recyclerId, UpdatePickupStatusRequest request);

    PickupResponse cancelPickup(Long pickupId, Long citizenId);
}
