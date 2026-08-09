package com.ecotrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
