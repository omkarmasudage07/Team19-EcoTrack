package com.ecotrack.material.dto.response;

import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.enums.RewardOrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardOrderResponse {
    private Long id;
    private String orderNumber;
    private Long citizenId;
    private Long rewardId;
    private String rewardTitle;
    private RewardCategory rewardCategory;
    private Integer pointsSpent;
    private String deliveryAddress;
    private String voucherCode;
    private RewardOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
