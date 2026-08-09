package com.ecotrack.material.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardReportResponse {
    private Long totalPointsIssued;
    private Long totalPointsRedeemed;
    private Long currentSystemBalance;
    private Long totalRewardsCount;
    private Long activeRewardsCount;
    private Long totalOrdersCount;
    private Long pendingOrdersCount;
    private Long confirmedOrdersCount;
    private Long deliveredOrdersCount;
    private String mostRedeemedReward;
    private List<TopRewardStat> topRewards;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopRewardStat {
        private String rewardTitle;
        private Long redemptionCount;
    }
}
