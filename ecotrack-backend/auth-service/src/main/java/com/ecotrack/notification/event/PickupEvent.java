package com.ecotrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PickupEvent implements Serializable {
    private Long pickupId;
    private String pickupNumber;
    private Long citizenId;
    private Long recyclerId;
    private String status;
    private LocalDateTime occurredAt;
}
