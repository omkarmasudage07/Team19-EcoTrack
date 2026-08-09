package com.ecotrack.notification.consumer;

import com.ecotrack.notification.constant.RabbitMQConstants;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.event.ApplicationApprovedEvent;
import com.ecotrack.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_RECYCLER_APPROVED)
    public void onRecyclerApproved(ApplicationApprovedEvent event) {
        notificationService.notify(
                event.getUserId(), RoleType.RECYCLER,
                "Application Approved",
                "Congratulations! " + event.getCompanyName() + " has been approved as a Recycler Partner. You can now log in.",
                NotificationType.RECYCLER_APPROVED);

        notificationService.logAudit("RECYCLER_APPROVED", event.getCompanyName() + " approved as Recycler Partner");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_INDUSTRY_APPROVED)
    public void onIndustryApproved(ApplicationApprovedEvent event) {
        notificationService.notify(
                event.getUserId(), RoleType.INDUSTRY,
                "Application Approved",
                "Congratulations! " + event.getCompanyName() + " has been approved as an Industrial Buyer. You can now log in.",
                NotificationType.INDUSTRY_APPROVED);

        notificationService.logAudit("INDUSTRY_APPROVED", event.getCompanyName() + " approved as Industrial Buyer");
    }
}
