package com.ecotrack.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequest {

    @NotBlank(message = "Region name is required")
    private String name;

    private String code;
    private String description;
    private Boolean active;
}
