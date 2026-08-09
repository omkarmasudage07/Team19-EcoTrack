package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.*;
import com.ecotrack.material.dto.response.*;
import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.enums.RewardOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RewardService {

    Page<RewardResponse> getRewards(Boolean active, RewardCategory category, boolean inStockOnly, String search, Pageable pageable);

    RewardResponse getRewardById(Long id);

    RewardResponse createReward(CreateRewardRequest request);

    RewardResponse updateReward(Long id, UpdateRewardRequest request);

    void deleteReward(Long id);

    RewardResponse toggleActive(Long id);

    RewardOrderResponse redeemReward(Long citizenId, Long rewardId, RedeemRewardRequest request);

    Page<RewardOrderResponse> getCitizenOrders(Long citizenId, Pageable pageable);

    Page<RewardOrderResponse> getAdminOrders(RewardOrderStatus status, Pageable pageable);

    RewardOrderResponse updateOrderStatus(Long orderId, RewardOrderStatus status);

    RewardReportResponse getAdminReportStats();
}
