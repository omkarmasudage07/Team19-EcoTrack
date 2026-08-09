package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.CreateRewardRequest;
import com.ecotrack.material.dto.request.RedeemRewardRequest;
import com.ecotrack.material.dto.response.RewardOrderResponse;
import com.ecotrack.material.dto.response.RewardResponse;
import com.ecotrack.material.entity.Reward;
import com.ecotrack.material.entity.RewardOrder;
import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.enums.RewardOrderStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.producer.RewardEventProducer;
import com.ecotrack.material.repository.EcoPointsWalletRepository;
import com.ecotrack.material.repository.RewardOrderRepository;
import com.ecotrack.material.repository.RewardRepository;
import com.ecotrack.material.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private RewardOrderRepository rewardOrderRepository;

    @Mock
    private EcoPointsWalletRepository walletRepository;

    @Mock
    private EcoPointsService ecoPointsService;

    @Mock
    private RewardEventProducer rewardEventProducer;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private Reward testReward;

    @BeforeEach
    void setUp() {
        testReward = Reward.builder()
                .id(10L)
                .title("Stainless Steel Bottle")
                .description("Insulated eco bottle")
                .category(RewardCategory.ECO_PRODUCT)
                .pointsRequired(200)
                .stockQuantity(10)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create reward successfully")
    void createReward_success() {
        CreateRewardRequest request = CreateRewardRequest.builder()
                .title("Eco Bag")
                .description("Cotton bag")
                .category(RewardCategory.ECO_PRODUCT)
                .pointsRequired(100)
                .stockQuantity(20)
                .active(true)
                .build();

        when(rewardRepository.save(any(Reward.class))).thenAnswer(i -> {
            Reward r = i.getArgument(0);
            r.setId(11L);
            return r;
        });

        RewardResponse response = rewardService.createReward(request);

        assertNotNull(response);
        assertEquals("Eco Bag", response.getTitle());
        assertEquals(100, response.getPointsRequired());
        verify(rewardRepository, times(1)).save(any(Reward.class));
    }

    @Test
    @DisplayName("Should redeem reward successfully, deduct points, decrement stock, publish event")
    void redeemReward_success() {
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(testReward));
        when(rewardRepository.save(any(Reward.class))).thenAnswer(i -> i.getArgument(0));
        when(rewardOrderRepository.save(any(RewardOrder.class))).thenAnswer(i -> {
            RewardOrder ro = i.getArgument(0);
            ro.setId(100L);
            return ro;
        });

        RedeemRewardRequest req = RedeemRewardRequest.builder()
                .deliveryAddress("123 Eco St, Pune")
                .build();

        RewardOrderResponse response = rewardService.redeemReward(50L, 10L, req);

        assertNotNull(response);
        assertEquals("Stainless Steel Bottle", response.getRewardTitle());
        assertEquals(200, response.getPointsSpent());
        assertEquals(RewardOrderStatus.CONFIRMED, response.getStatus());
        assertEquals(9, testReward.getStockQuantity()); // Stock decremented

        verify(ecoPointsService, times(1)).deductPoints(eq(50L), eq(200), anyString());
        verify(rewardEventProducer, times(1)).publishRewardRedeemed(any(RewardOrder.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to redeem out-of-stock reward")
    void redeemReward_outOfStock_throwsException() {
        testReward.setStockQuantity(0);
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(testReward));

        assertThrows(BusinessException.class, () -> rewardService.redeemReward(50L, 10L, new RedeemRewardRequest()));
        verify(ecoPointsService, never()).deductPoints(anyLong(), anyInt(), anyString());
        verify(rewardOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to redeem inactive reward")
    void redeemReward_inactive_throwsException() {
        testReward.setActive(false);
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(testReward));

        assertThrows(BusinessException.class, () -> rewardService.redeemReward(50L, 10L, new RedeemRewardRequest()));
        verify(ecoPointsService, never()).deductPoints(anyLong(), anyInt(), anyString());
        verify(rewardOrderRepository, never()).save(any());
    }
}
