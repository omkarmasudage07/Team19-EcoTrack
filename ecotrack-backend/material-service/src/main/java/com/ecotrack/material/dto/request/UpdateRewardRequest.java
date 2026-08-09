package com.ecotrack.material.dto.request;

import com.ecotrack.material.enums.RewardCategory;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRewardRequest {

    private String title;
    private String description;
    private RewardCategory category;

    @Min(value = 1, message = "Points required must be at least 1")
    private Integer pointsRequired;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private String imageUrl;
    private Boolean active;
}
