package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {
    private Long id;
    private Long recyclerId;
    private Long categoryId;
    private String categoryName;
    private String materialName;
    private String description;
    private String purity;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal pricePerUnit;
    private String warehouseLocation;
    private AvailabilityStatus availabilityStatus;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
