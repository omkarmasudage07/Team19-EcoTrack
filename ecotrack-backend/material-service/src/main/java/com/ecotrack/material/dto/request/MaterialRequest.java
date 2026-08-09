package com.ecotrack.material.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Material name is required")
    private String materialName;

    private String description;

    private String purity;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price per unit must be greater than zero")
    private BigDecimal pricePerUnit;

    private String warehouseLocation;

    private List<String> imageUrls;
}
