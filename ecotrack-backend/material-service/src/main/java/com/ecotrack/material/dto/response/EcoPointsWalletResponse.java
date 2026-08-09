package com.ecotrack.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoPointsWalletResponse {
    private Long citizenId;
    private Integer currentBalance;
    private Integer totalEarned;
    private Integer totalRedeemed;
}
