package com.ecotrack.material.producer;

import com.ecotrack.material.constant.RabbitMQConstants;
import com.ecotrack.material.entity.RewardOrder;
import com.ecotrack.material.event.RewardRedeemedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RewardEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishRewardRedeemed(RewardOrder order) {
        RewardRedeemedEvent event = RewardRedeemedEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .citizenId(order.getCitizenId())
                .rewardId(order.getReward().getId())
                .rewardTitle(order.getRewardTitle())
                .pointsSpent(order.getPointsSpent())
                .voucherCode(order.getVoucherCode())
                .redeemedAt(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.EXCHANGE_NAME,
                    RabbitMQConstants.ROUTING_KEY_REWARD_REDEEMED,
                    event);
            log.info("Published reward.redeemed event for order {}", order.getOrderNumber());
        } catch (Exception ex) {
            log.error("Failed to publish reward.redeemed event for order {}: {}", order.getOrderNumber(), ex.getMessage());
        }
    }
}
