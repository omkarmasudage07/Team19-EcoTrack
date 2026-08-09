package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.CreateRewardRequest;
import com.ecotrack.material.dto.request.RedeemRewardRequest;
import com.ecotrack.material.dto.request.UpdateRewardRequest;
import com.ecotrack.material.dto.response.RewardOrderResponse;
import com.ecotrack.material.dto.response.RewardReportResponse;
import com.ecotrack.material.dto.response.RewardResponse;
import com.ecotrack.material.entity.EcoPointsWallet;
import com.ecotrack.material.entity.Reward;
import com.ecotrack.material.entity.RewardOrder;
import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.enums.RewardOrderStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.producer.RewardEventProducer;
import com.ecotrack.material.repository.EcoPointsWalletRepository;
import com.ecotrack.material.repository.RewardOrderRepository;
import com.ecotrack.material.repository.RewardRepository;
import com.ecotrack.material.service.EcoPointsService;
import com.ecotrack.material.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardOrderRepository rewardOrderRepository;
    private final EcoPointsWalletRepository walletRepository;
    private final EcoPointsService ecoPointsService;
    private final RewardEventProducer rewardEventProducer;

    @Override
    public Page<RewardResponse> getRewards(Boolean active, RewardCategory category, boolean inStockOnly, String search, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        return rewardRepository.searchRewards(active, category, inStockOnly, cleanSearch, pageable)
                .map(this::toResponse);
    }

    @Override
    public RewardResponse getRewardById(Long id) {
        Reward reward = findRewardOrThrow(id);
        return toResponse(reward);
    }

    @Override
    @Transactional
    public RewardResponse createReward(CreateRewardRequest request) {
        Reward reward = Reward.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .category(request.getCategory())
                .pointsRequired(request.getPointsRequired())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        reward = rewardRepository.save(reward);
        log.info("Created reward item: {} ({} pts)", reward.getTitle(), reward.getPointsRequired());
        return toResponse(reward);
    }

    @Override
    @Transactional
    public RewardResponse updateReward(Long id, UpdateRewardRequest request) {
        Reward reward = findRewardOrThrow(id);

        if (request.getTitle() != null && !request.getTitle().isBlank()) reward.setTitle(request.getTitle().trim());
        if (request.getDescription() != null) reward.setDescription(request.getDescription());
        if (request.getCategory() != null) reward.setCategory(request.getCategory());
        if (request.getPointsRequired() != null) reward.setPointsRequired(request.getPointsRequired());
        if (request.getStockQuantity() != null) reward.setStockQuantity(request.getStockQuantity());
        if (request.getImageUrl() != null) reward.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) reward.setActive(request.getActive());

        reward = rewardRepository.save(reward);
        log.info("Updated reward item id: {}", id);
        return toResponse(reward);
    }

    @Override
    @Transactional
    public void deleteReward(Long id) {
        Reward reward = findRewardOrThrow(id);
        rewardRepository.delete(reward);
        log.info("Deleted reward item id: {}", id);
    }

    @Override
    @Transactional
    public RewardResponse toggleActive(Long id) {
        Reward reward = findRewardOrThrow(id);
        reward.setActive(!reward.isActive());
        reward = rewardRepository.save(reward);
        log.info("Toggled active status of reward id {} to {}", id, reward.isActive());
        return toResponse(reward);
    }

    @Override
    @Transactional
    public RewardOrderResponse redeemReward(Long citizenId, Long rewardId, RedeemRewardRequest request) {
        Reward reward = findRewardOrThrow(rewardId);

        if (!reward.isActive()) {
            throw new BusinessException("This reward is currently inactive and cannot be redeemed.", HttpStatus.BAD_REQUEST);
        }

        if (reward.getStockQuantity() <= 0) {
            throw new BusinessException("Reward is out of stock.", HttpStatus.BAD_REQUEST);
        }

        // Deduct points from wallet (validates available balance)
        ecoPointsService.deductPoints(citizenId, reward.getPointsRequired(), "Redeemed reward: " + reward.getTitle());

        // Decrement stock
        reward.setStockQuantity(reward.getStockQuantity() - 1);
        rewardRepository.save(reward);

        // Generate Order Number & Voucher Code
        String orderNumber = "RWD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000);
        String voucherCode = null;
        if (reward.getCategory() == RewardCategory.GIFT_CARD || 
            reward.getCategory() == RewardCategory.DISCOUNT_COUPON || 
            reward.getCategory() == RewardCategory.CERTIFICATE) {
            voucherCode = "ECO-VO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        String deliveryAddr = (request != null && request.getDeliveryAddress() != null && !request.getDeliveryAddress().isBlank())
                ? request.getDeliveryAddress() : "Digital Fulfillment / Default Profile Address";

        RewardOrder order = RewardOrder.builder()
                .orderNumber(orderNumber)
                .citizenId(citizenId)
                .reward(reward)
                .rewardTitle(reward.getTitle())
                .pointsSpent(reward.getPointsRequired())
                .deliveryAddress(deliveryAddr)
                .voucherCode(voucherCode)
                .status(RewardOrderStatus.CONFIRMED)
                .build();

        order = rewardOrderRepository.save(order);
        log.info("Citizen {} successfully redeemed reward '{}' for {} points. Order #{}", 
                citizenId, reward.getTitle(), reward.getPointsRequired(), order.getOrderNumber());

        // Publish RabbitMQ Event
        rewardEventProducer.publishRewardRedeemed(order);

        return toResponse(order);
    }

    @Override
    public Page<RewardOrderResponse> getCitizenOrders(Long citizenId, Pageable pageable) {
        return rewardOrderRepository.findByCitizenId(citizenId, pageable).map(this::toResponse);
    }

    @Override
    public Page<RewardOrderResponse> getAdminOrders(RewardOrderStatus status, Pageable pageable) {
        if (status != null) {
            return rewardOrderRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        return rewardOrderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public RewardOrderResponse updateOrderStatus(Long orderId, RewardOrderStatus status) {
        RewardOrder order = rewardOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward Order not found with id: " + orderId));

        order.setStatus(status);
        order = rewardOrderRepository.save(order);
        log.info("Updated status of Reward Order #{} to {}", order.getOrderNumber(), status);
        return toResponse(order);
    }

    @Override
    public RewardReportResponse getAdminReportStats() {
        Long totalEarned = walletRepository.findAll().stream()
                .mapToLong(w -> w.getTotalEarned() != null ? w.getTotalEarned() : 0)
                .sum();

        Long totalRedeemed = walletRepository.findAll().stream()
                .mapToLong(w -> w.getTotalRedeemed() != null ? w.getTotalRedeemed() : 0)
                .sum();

        Long currentBalance = walletRepository.findAll().stream()
                .mapToLong(w -> w.getCurrentBalance() != null ? w.getCurrentBalance() : 0)
                .sum();

        long totalRewards = rewardRepository.count();
        long activeRewards = rewardRepository.countByActiveTrue();
        long totalOrders = rewardOrderRepository.count();
        long pendingOrders = rewardOrderRepository.countByStatus(RewardOrderStatus.PENDING);
        long confirmedOrders = rewardOrderRepository.countByStatus(RewardOrderStatus.CONFIRMED);
        long deliveredOrders = rewardOrderRepository.countByStatus(RewardOrderStatus.DELIVERED);

        List<Object[]> topRewardRows = rewardOrderRepository.findMostRedeemedRewards(PageRequest.of(0, 5));
        List<RewardReportResponse.TopRewardStat> topStats = new ArrayList<>();
        String mostRedeemed = "N/A";

        if (!topRewardRows.isEmpty()) {
            mostRedeemed = (String) topRewardRows.get(0)[0];
            for (Object[] row : topRewardRows) {
                topStats.add(new RewardReportResponse.TopRewardStat((String) row[0], (Long) row[1]));
            }
        }

        return RewardReportResponse.builder()
                .totalPointsIssued(totalEarned)
                .totalPointsRedeemed(totalRedeemed)
                .currentSystemBalance(currentBalance)
                .totalRewardsCount(totalRewards)
                .activeRewardsCount(activeRewards)
                .totalOrdersCount(totalOrders)
                .pendingOrdersCount(pendingOrders)
                .confirmedOrdersCount(confirmedOrders)
                .deliveredOrdersCount(deliveredOrders)
                .mostRedeemedReward(mostRedeemed)
                .topRewards(topStats)
                .build();
    }

    private Reward findRewardOrThrow(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward item not found with id: " + id));
    }

    private RewardResponse toResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .title(reward.getTitle())
                .description(reward.getDescription())
                .category(reward.getCategory())
                .pointsRequired(reward.getPointsRequired())
                .stockQuantity(reward.getStockQuantity())
                .imageUrl(reward.getImageUrl())
                .active(reward.isActive())
                .createdAt(reward.getCreatedAt())
                .updatedAt(reward.getUpdatedAt())
                .build();
    }

    private RewardOrderResponse toResponse(RewardOrder order) {
        return RewardOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .citizenId(order.getCitizenId())
                .rewardId(order.getReward() != null ? order.getReward().getId() : null)
                .rewardTitle(order.getRewardTitle())
                .rewardCategory(order.getReward() != null ? order.getReward().getCategory() : null)
                .pointsSpent(order.getPointsSpent())
                .deliveryAddress(order.getDeliveryAddress())
                .voucherCode(order.getVoucherCode())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
