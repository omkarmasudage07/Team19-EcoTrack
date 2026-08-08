package com.ecotrack.pickup.service.impl;

import com.ecotrack.pickup.client.UserServiceClient;
import com.ecotrack.pickup.dto.request.SchedulePickupRequest;
import com.ecotrack.pickup.dto.request.UpdatePickupStatusRequest;
import com.ecotrack.pickup.dto.response.PickupResponse;
import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.entity.PickupImage;
import com.ecotrack.pickup.entity.PickupStatusHistory;
import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.enums.PickupStatus;
import com.ecotrack.pickup.exception.BusinessException;
import com.ecotrack.pickup.exception.ResourceNotFoundException;
import com.ecotrack.pickup.mapper.PickupMapper;
import com.ecotrack.pickup.producer.PickupEventProducer;
import com.ecotrack.pickup.repository.PickupImageRepository;
import com.ecotrack.pickup.repository.PickupRepository;
import com.ecotrack.pickup.repository.PickupStatusHistoryRepository;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import com.ecotrack.pickup.service.PickupService;
import com.ecotrack.pickup.util.PickupNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickupServiceImpl implements PickupService {

    private final PickupRepository pickupRepository;
    private final PickupImageRepository pickupImageRepository;
    private final PickupStatusHistoryRepository statusHistoryRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final UserServiceClient userServiceClient;
    private final PickupEventProducer pickupEventProducer;

    /** Exactly which status is allowed to follow which - enforced on every transition. */
    private static final Map<PickupStatus, PickupStatus> NEXT_STATUS = Map.of(
            PickupStatus.ACCEPTED, PickupStatus.ON_THE_WAY,
            PickupStatus.ON_THE_WAY, PickupStatus.COLLECTED,
            PickupStatus.COLLECTED, PickupStatus.PROCESSING,
            PickupStatus.PROCESSING, PickupStatus.COMPLETED
    );

    // ------------------------------------------------------------------
    // Citizen
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public PickupResponse schedulePickup(Long citizenId, SchedulePickupRequest request) {
        WasteCategory category = wasteCategoryRepository.findById(request.getWasteCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste category not found"));

        if (!category.isActive()) {
            throw new BusinessException("This waste category is no longer available", HttpStatus.BAD_REQUEST);
        }

        Pickup pickup = Pickup.builder()
                .pickupNumber(PickupNumberGenerator.generate())
                .citizenId(citizenId)
                .pickupAddress(request.getPickupAddress())
                .pickupCity(request.getPickupCity())
                .pickupPincode(request.getPickupPincode())
                .regionName(request.getRegionName() != null && !request.getRegionName().isBlank() ? request.getRegionName() : "Pune Region")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pickupDate(request.getPickupDate())
                .pickupTimeSlot(request.getPickupTimeSlot())
                .wasteCategory(category)
                .notes(request.getNotes())
                .status(PickupStatus.PENDING)
                .build();

        pickup = pickupRepository.save(pickup);
        recordHistory(pickup, null, PickupStatus.PENDING, citizenId);

        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                pickupImageRepository.save(PickupImage.builder()
                        .pickup(pickup)
                        .imageUrl(url)
                        .imageType("WASTE_PHOTO")
                        .build());
            }
        }

        log.info("Pickup {} scheduled by citizen {} in region {}", pickup.getPickupNumber(), citizenId, pickup.getRegionName());
        return getPickupDetail(pickup.getId());
    }

    @Override
    public PickupResponse getPickupDetail(Long pickupId) {
        Pickup pickup = findPickupOrThrow(pickupId);
        List<PickupImage> images = pickupImageRepository.findByPickupId(pickupId);
        List<PickupStatusHistory> history = statusHistoryRepository.findByPickupIdOrderByUpdatedAtAsc(pickupId);
        return PickupMapper.toDetailResponse(pickup, images, history);
    }

    @Override
    public Page<PickupResponse> getCitizenPickups(Long citizenId, PickupStatus status, Pageable pageable) {
        Page<Pickup> pickups = (status != null)
                ? pickupRepository.findByCitizenIdAndStatus(citizenId, status, pageable)
                : pickupRepository.findByCitizenId(citizenId, pageable);
        return pickups.map(PickupMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public PickupResponse cancelPickup(Long pickupId, Long citizenId) {
        Pickup pickup = findPickupOrThrow(pickupId);

        if (!pickup.getCitizenId().equals(citizenId)) {
            throw new BusinessException("You can only cancel your own pickups", HttpStatus.FORBIDDEN);
        }
        if (pickup.getStatus() != PickupStatus.PENDING) {
            throw new BusinessException(
                    "This pickup can no longer be cancelled - a Recycler has already accepted it", HttpStatus.CONFLICT);
        }

        PickupStatus oldStatus = pickup.getStatus();
        pickup.setStatus(PickupStatus.CANCELLED);
        pickup = pickupRepository.save(pickup);
        recordHistory(pickup, oldStatus, PickupStatus.CANCELLED, citizenId);
        pickupEventProducer.publishPickupCancelled(pickup);

        return getPickupDetail(pickup.getId());
    }

    // ------------------------------------------------------------------
    // Recycler
    // ------------------------------------------------------------------

    @Override
    public Page<PickupResponse> getAvailablePickups(String regionName, Double recyclerLat, Double recyclerLng, Pageable pageable) {
        Page<Pickup> pickups = (regionName != null && !regionName.isBlank())
                ? pickupRepository.findByStatusAndRecyclerIdIsNullAndRegionNameIgnoreCase(PickupStatus.PENDING, regionName.trim(), pageable)
                : pickupRepository.findByStatusAndRecyclerIdIsNull(PickupStatus.PENDING, pageable);

        return pickups.map(pickup -> {
            PickupResponse res = PickupMapper.toSummaryResponse(pickup);
            if (recyclerLat != null && recyclerLng != null && pickup.getLatitude() != null && pickup.getLongitude() != null) {
                double dist = calculateDistanceKm(recyclerLat, recyclerLng, pickup.getLatitude(), pickup.getLongitude());
                res.setDistanceKm(Math.round(dist * 100.0) / 100.0);
            }
            return res;
        });
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public Page<PickupResponse> getRecyclerPickups(Long recyclerId, PickupStatus status, Pageable pageable) {
        Page<Pickup> pickups = (status != null)
                ? pickupRepository.findByRecyclerIdAndStatus(recyclerId, status, pageable)
                : pickupRepository.findByRecyclerId(recyclerId, pageable);
        return pickups.map(PickupMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public PickupResponse acceptPickup(Long pickupId, Long recyclerId) {
        Pickup pickup = findPickupOrThrow(pickupId);

        if (pickup.getStatus() != PickupStatus.PENDING || pickup.getRecyclerId() != null) {
            throw new BusinessException("This pickup has already been accepted by another Recycler", HttpStatus.CONFLICT);
        }

        pickup.setRecyclerId(recyclerId);
        pickup.setStatus(PickupStatus.ACCEPTED);
        pickup = pickupRepository.save(pickup);
        recordHistory(pickup, PickupStatus.PENDING, PickupStatus.ACCEPTED, recyclerId);
        pickupEventProducer.publishPickupAccepted(pickup);

        log.info("Pickup {} accepted by recycler {}", pickup.getPickupNumber(), recyclerId);
        return getPickupDetail(pickup.getId());
    }

    @Override
    public PickupResponse rejectPickup(Long pickupId, Long recyclerId) {
        // A "reject" by one Recycler does not change the Pickup's state -
        // it simply remains PENDING and visible to every other Recycler.
        // There is nothing to persist here; this endpoint exists purely
        // so the frontend can remove the card from that Recycler's own
        // "available pickups" list.
        Pickup pickup = findPickupOrThrow(pickupId);
        log.info("Pickup {} declined by recycler {} - remains available for others", pickup.getPickupNumber(), recyclerId);
        return getPickupDetail(pickupId);
    }

    @Override
    @Transactional
    public PickupResponse updateStatus(Long pickupId, Long recyclerId, UpdatePickupStatusRequest request) {
        Pickup pickup = findPickupOrThrow(pickupId);

        if (!recyclerId.equals(pickup.getRecyclerId())) {
            throw new BusinessException("You are not assigned to this pickup", HttpStatus.FORBIDDEN);
        }

        PickupStatus currentStatus = pickup.getStatus();
        PickupStatus expectedNext = NEXT_STATUS.get(currentStatus);

        if (expectedNext == null || expectedNext != request.getStatus()) {
            throw new BusinessException(
                    "Invalid status transition: a pickup that is " + currentStatus +
                            " can only move to " + (expectedNext != null ? expectedNext : "no further status"),
                    HttpStatus.BAD_REQUEST);
        }

        pickup.setStatus(request.getStatus());
        pickup = pickupRepository.save(pickup);
        recordHistory(pickup, currentStatus, request.getStatus(), recyclerId);

        if (request.getStatus() == PickupStatus.COLLECTED && request.getProofImageUrl() != null) {
            pickupImageRepository.save(PickupImage.builder()
                    .pickup(pickup)
                    .imageUrl(request.getProofImageUrl())
                    .imageType("COLLECTION_PROOF")
                    .build());
        }

        if (request.getStatus() == PickupStatus.COMPLETED) {
            // This is the trigger the business workflow describes: once a
            // pickup is completed, the Citizen earns EcoPoints. Material
            // Exchange Service will consume this event and credit the
            // wallet once it exists.
            pickupEventProducer.publishPickupCompleted(pickup);
        }

        log.info("Pickup {} status changed {} -> {}", pickup.getPickupNumber(), currentStatus, request.getStatus());
        return getPickupDetail(pickup.getId());
    }

    // ------------------------------------------------------------------
    // Admin
    // ------------------------------------------------------------------

    @Override
    public Page<PickupResponse> getAllPickups(PickupStatus status, Pageable pageable) {
        Page<Pickup> pickups = (status != null)
                ? pickupRepository.findByStatus(status, pageable)
                : pickupRepository.findAll(pageable);
        return pickups.map(PickupMapper::toSummaryResponse);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Pickup findPickupOrThrow(Long pickupId) {
        return pickupRepository.findById(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup not found with id: " + pickupId));
    }

    private void recordHistory(Pickup pickup, PickupStatus oldStatus, PickupStatus newStatus, Long updatedBy) {
        statusHistoryRepository.save(PickupStatusHistory.builder()
                .pickup(pickup)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .updatedBy(updatedBy)
                .build());
    }
}
