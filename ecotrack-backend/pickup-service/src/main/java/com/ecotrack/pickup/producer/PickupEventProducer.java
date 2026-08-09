package com.ecotrack.pickup.producer;

import com.ecotrack.pickup.constant.RabbitMQConstants;
import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.event.PickupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickupEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishPickupAccepted(Pickup pickup) {
        publish(RabbitMQConstants.ROUTING_KEY_PICKUP_ACCEPTED, pickup);
    }

    public void publishPickupCompleted(Pickup pickup) {
        publish(RabbitMQConstants.ROUTING_KEY_PICKUP_COMPLETED, pickup);
    }

    public void publishPickupCancelled(Pickup pickup) {
        publish(RabbitMQConstants.ROUTING_KEY_PICKUP_CANCELLED, pickup);
    }

    private void publish(String routingKey, Pickup pickup) {
        PickupEvent event = PickupEvent.builder()
                .pickupId(pickup.getId())
                .pickupNumber(pickup.getPickupNumber())
                .citizenId(pickup.getCitizenId())
                .recyclerId(pickup.getRecyclerId())
                .status(pickup.getStatus())
                .wasteCategoryName(pickup.getWasteCategory() != null ? pickup.getWasteCategory().getName() : null)
                .occurredAt(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, routingKey, event);
            log.info("Published event [{}] for pickup {}", routingKey, pickup.getPickupNumber());
        } catch (Exception ex) {
            // A pickup status change should never fail just because
            // RabbitMQ happens to be down - we log it and move on rather
            // than rolling back a valid, already-persisted status change.
            log.error("Failed to publish event [{}] for pickup {}: {}", routingKey, pickup.getPickupNumber(), ex.getMessage());
        }
    }
}
