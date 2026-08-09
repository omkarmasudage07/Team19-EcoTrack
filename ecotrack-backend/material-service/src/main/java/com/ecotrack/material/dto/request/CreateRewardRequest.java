package com.ecotrack.material.dto.request;

import com.ecotrack.material.enums.RewardCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRewardRequest {

    @NotBlank(message = "Reward title is required")
    private String title;

    private String description;

    @NotNull(message = "Reward category is required")
    private RewardCategory category;

    @NotNull(message = "Points required must be specified")
    @Min(value = 1, message = "Points required must be at least 1")
    private Integer pointsRequired;

    @NotNull(message = "Stock quantity must be specified")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private String imageUrl;

    private Boolean active;
}
