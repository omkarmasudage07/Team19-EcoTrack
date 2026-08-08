package com.ecotrack.pickup.dto.request;

import com.ecotrack.pickup.enums.PickupStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePickupStatusRequest {

    @NotNull(message = "New status is required")
    private PickupStatus status;

    /** Only used when status = COLLECTED, to attach the collection-proof photo. */
    private String proofImageUrl;
}
