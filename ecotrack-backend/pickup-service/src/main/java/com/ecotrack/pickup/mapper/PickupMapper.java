package com.ecotrack.pickup.mapper;

import com.ecotrack.pickup.dto.response.*;
import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.entity.PickupImage;
import com.ecotrack.pickup.entity.PickupStatusHistory;
import com.ecotrack.pickup.entity.WasteCategory;

import java.util.List;
import java.util.stream.Collectors;

public final class PickupMapper {

    private PickupMapper() {
    }

    public static WasteCategoryResponse toResponse(WasteCategory category) {
        return WasteCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .build();
    }

    public static PickupImageResponse toResponse(PickupImage image) {
        return PickupImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .imageType(image.getImageType())
                .build();
    }

    public static PickupStatusHistoryResponse toResponse(PickupStatusHistory history) {
        return PickupStatusHistoryResponse.builder()
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .updatedBy(history.getUpdatedBy())
                .updatedAt(history.getUpdatedAt())
                .build();
    }

    public static PickupResponse toSummaryResponse(Pickup pickup) {
        return baseBuilder(pickup).build();
    }

    public static PickupResponse toDetailResponse(Pickup pickup, List<PickupImage> images, List<PickupStatusHistory> history) {
        return baseBuilder(pickup)
                .images(images.stream().map(PickupMapper::toResponse).collect(Collectors.toList()))
                .statusHistory(history.stream().map(PickupMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    private static PickupResponse.PickupResponseBuilder baseBuilder(Pickup pickup) {
        return PickupResponse.builder()
                .id(pickup.getId())
                .pickupNumber(pickup.getPickupNumber())
                .citizenId(pickup.getCitizenId())
                .recyclerId(pickup.getRecyclerId())
                .pickupAddress(pickup.getPickupAddress())
                .pickupCity(pickup.getPickupCity())
                .pickupPincode(pickup.getPickupPincode())
                .regionName(pickup.getRegionName())
                .latitude(pickup.getLatitude())
                .longitude(pickup.getLongitude())
                .pickupDate(pickup.getPickupDate())
                .pickupTimeSlot(pickup.getPickupTimeSlot())
                .wasteCategoryId(pickup.getWasteCategory().getId())
                .wasteCategoryName(pickup.getWasteCategory().getName())
                .notes(pickup.getNotes())
                .status(pickup.getStatus())
                .createdAt(pickup.getCreatedAt())
                .updatedAt(pickup.getUpdatedAt());
    }
}
