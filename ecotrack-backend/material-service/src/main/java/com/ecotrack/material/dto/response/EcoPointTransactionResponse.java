package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.EcoPointTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoPointTransactionResponse {
    private Integer points;
    private EcoPointTransactionType transactionType;
    private String description;
    private LocalDateTime transactionDate;
}
