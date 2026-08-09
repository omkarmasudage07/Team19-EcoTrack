package com.ecotrack.material.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRedeemedEvent implements Serializable {
    private Long orderId;
    private String orderNumber;
    private Long citizenId;
    private Long rewardId;
    private String rewardTitle;
    private Integer pointsSpent;
    private String voucherCode;
    private LocalDateTime redeemedAt;
}
