package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.RewardCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {
    private Long id;
    private String title;
    private String description;
    private RewardCategory category;
    private Integer pointsRequired;
    private Integer stockQuantity;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
