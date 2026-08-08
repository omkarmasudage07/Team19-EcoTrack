package com.ecotrack.pickup.event;

import com.ecotrack.pickup.enums.PickupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Published whenever a Pickup reaches a milestone status. Notification
 * Service listens to these to create in-app notifications; once Material
 * Exchange Service exists, it will also listen for PICKUP_COMPLETED
 * events to award EcoPoints to the Citizen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupEvent implements Serializable {
    private Long pickupId;
    private String pickupNumber;
    private Long citizenId;
    private Long recyclerId;
    private PickupStatus status;
    private String wasteCategoryName;
    private LocalDateTime occurredAt;
}
