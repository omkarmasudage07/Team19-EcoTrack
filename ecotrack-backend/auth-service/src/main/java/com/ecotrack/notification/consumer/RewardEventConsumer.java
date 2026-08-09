package com.ecotrack.notification.consumer;

import com.ecotrack.notification.constant.RabbitMQConstants;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.event.RewardRedeemedEvent;
import com.ecotrack.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RewardEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_REWARD_REDEEMED)
    public void onRewardRedeemed(RewardRedeemedEvent event) {
        log.info("Received reward.redeemed event for order {} (Citizen {})", event.getOrderNumber(), event.getCitizenId());

        String message = "Congratulations! You successfully redeemed '" + event.getRewardTitle() +
                "' for " + event.getPointsSpent() + " EcoPoints. Order #" + event.getOrderNumber() +
                (event.getVoucherCode() != null ? " (Voucher Code: " + event.getVoucherCode() + ")" : "");

        notificationService.notify(
                event.getCitizenId(),
                RoleType.CITIZEN,
                "Reward Redeemed Successfully!",
                message,
                NotificationType.REWARD_REDEEMED);

        notificationService.logAudit("REWARD_REDEEMED", "Reward order #" + event.getOrderNumber() + " redeemed");
    }
}
