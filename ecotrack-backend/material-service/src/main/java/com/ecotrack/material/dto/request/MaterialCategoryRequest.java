package com.ecotrack.material.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
