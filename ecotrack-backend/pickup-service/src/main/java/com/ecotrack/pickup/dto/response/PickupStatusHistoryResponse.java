package com.ecotrack.pickup.dto.response;

import com.ecotrack.pickup.enums.PickupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupStatusHistoryResponse {
    private PickupStatus oldStatus;
    private PickupStatus newStatus;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
