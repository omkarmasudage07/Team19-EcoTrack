package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.enums.PaymentStatus;
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
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long industryId;
    private Long recyclerId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
}
