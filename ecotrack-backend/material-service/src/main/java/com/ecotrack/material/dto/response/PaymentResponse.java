package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.PaymentMethod;
import com.ecotrack.material.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String transactionNumber;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal paidAmount;
    private LocalDateTime paymentDate;
    private String remarks;
}
