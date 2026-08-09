package com.ecotrack.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long materialId;
    private String materialName;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
