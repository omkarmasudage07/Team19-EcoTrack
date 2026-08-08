package com.ecotrack.pickup.dto.response;

import com.ecotrack.pickup.enums.PickupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupResponse {
    private Long id;
    private String pickupNumber;
    private Long citizenId;
    private Long recyclerId;
    private String pickupAddress;
    private String pickupCity;
    private String pickupPincode;
    private String regionName;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private LocalDate pickupDate;
    private String pickupTimeSlot;
    private Long wasteCategoryId;
    private String wasteCategoryName;
    private String notes;
    private PickupStatus status;
    private List<PickupImageResponse> images;
    private List<PickupStatusHistoryResponse> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
