package com.ecotrack.pickup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupImageResponse {
    private Long id;
    private String imageUrl;
    private String imageType;
}
