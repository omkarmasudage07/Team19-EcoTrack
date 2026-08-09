package com.ecotrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {
    private Long orderId;
    private String orderNumber;
    private Long industryId;
    private Long recyclerId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime occurredAt;
}
