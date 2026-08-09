package com.ecotrack.notification.consumer;

import com.ecotrack.notification.constant.RabbitMQConstants;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.event.PickupEvent;
import com.ecotrack.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickupEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PICKUP_ACCEPTED)
    public void onPickupAccepted(PickupEvent event) {
        notificationService.notify(
                event.getCitizenId(), RoleType.CITIZEN,
                "Pickup Accepted",
                "Your pickup " + event.getPickupNumber() + " has been accepted by a Recycler.",
                NotificationType.PICKUP_ACCEPTED);

        if (event.getRecyclerId() != null) {
            notificationService.notify(
                    event.getRecyclerId(), RoleType.RECYCLER,
                    "New Pickup Assigned",
                    "You accepted pickup " + event.getPickupNumber() + ".",
                    NotificationType.PICKUP_ACCEPTED);
        }

        notificationService.logAudit("PICKUP_ACCEPTED", "Pickup " + event.getPickupNumber() + " accepted");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PICKUP_COMPLETED)
    public void onPickupCompleted(PickupEvent event) {
        notificationService.notify(
                event.getCitizenId(), RoleType.CITIZEN,
                "Pickup Completed",
                "Your pickup " + event.getPickupNumber() + " is complete. EcoPoints have been added to your wallet!",
                NotificationType.PICKUP_COMPLETED);

        notificationService.logAudit("PICKUP_COMPLETED", "Pickup " + event.getPickupNumber() + " completed");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PICKUP_CANCELLED)
    public void onPickupCancelled(PickupEvent event) {
        notificationService.notify(
                event.getCitizenId(), RoleType.CITIZEN,
                "Pickup Cancelled",
                "Your pickup " + event.getPickupNumber() + " has been cancelled.",
                NotificationType.PICKUP_CANCELLED);

        notificationService.logAudit("PICKUP_CANCELLED", "Pickup " + event.getPickupNumber() + " cancelled");
    }
}
