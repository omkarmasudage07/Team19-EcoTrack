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
public class WasteCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;
}
