package com.ecotrack.material.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EcoPointRuleRequest {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Points per unit is required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer pointsPerUnit;

    private String ruleType; // FLAT, PER_KG, etc.
    private String description;
    private Boolean active;
}
