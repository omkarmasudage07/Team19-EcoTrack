package com.ecotrack.material.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PlaceOrderRequest {

    /** All items in one order must belong to the same Recycler - validated in the service layer. */
    @NotEmpty(message = "An order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "materialId is required")
        private Long materialId;

        @NotNull(message = "quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        private BigDecimal quantity;
    }
}
