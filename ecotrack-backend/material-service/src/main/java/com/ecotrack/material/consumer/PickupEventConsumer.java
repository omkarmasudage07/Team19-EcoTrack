package com.ecotrack.material.consumer;

import com.ecotrack.material.constant.RabbitMQConstants;
import com.ecotrack.material.event.PickupCompletedEvent;
import com.ecotrack.material.service.EcoPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens for "pickup.completed" events published by Pickup Service. This
 * is what actually implements the business rule "Citizen receives
 * EcoPoints after successful recycling" - Pickup Service only announces
 * completion, this service decides the reward and credits the wallet.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PickupEventConsumer {

    private final EcoPointsService ecoPointsService;
    private final com.ecotrack.material.service.EcoPointRuleService ecoPointRuleService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_MATERIAL_PICKUP_COMPLETED)
    public void onPickupCompleted(PickupCompletedEvent event) {
        log.info("Received pickup.completed event for pickup {}, category: {}",
                event.getPickupNumber(), event.getWasteCategoryName());

        int points = ecoPointRuleService.calculatePointsForCategory(event.getWasteCategoryName());
        String categoryLabel = (event.getWasteCategoryName() != null && !event.getWasteCategoryName().isBlank())
                ? event.getWasteCategoryName() : "E-Waste";

        String description = "Earned " + points + " EcoPoints for completed pickup #" + event.getPickupNumber() + " (" + categoryLabel + ")";
        ecoPointsService.awardPoints(event.getCitizenId(), points, description);
    }
}
