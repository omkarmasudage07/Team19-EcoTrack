package com.ecotrack.material.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Mirrors Pickup Service's PickupEvent payload shape - each service keeps
 * its own local copy of an event contract it depends on, same as with
 * REST DTOs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PickupCompletedEvent implements Serializable {
    private Long pickupId;
    private String pickupNumber;
    private Long citizenId;
    private Long recyclerId;
    private String status;
    private String wasteCategoryName;
    private LocalDateTime occurredAt;
}
