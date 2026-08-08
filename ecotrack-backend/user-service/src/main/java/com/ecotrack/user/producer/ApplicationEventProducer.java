package com.ecotrack.user.producer;

import com.ecotrack.user.constant.RabbitMQConstants;
import com.ecotrack.user.event.ApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishRecyclerApproved(Long userId, String companyName, String email) {
        publish(RabbitMQConstants.ROUTING_KEY_RECYCLER_APPROVED, userId, companyName, email);
    }

    public void publishIndustryApproved(Long userId, String companyName, String email) {
        publish(RabbitMQConstants.ROUTING_KEY_INDUSTRY_APPROVED, userId, companyName, email);
    }

    private void publish(String routingKey, Long userId, String companyName, String email) {
        ApplicationApprovedEvent event = ApplicationApprovedEvent.builder()
                .userId(userId)
                .companyName(companyName)
                .email(email)
                .occurredAt(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, routingKey, event);
            log.info("Published event [{}] for {} ({})", routingKey, companyName, email);
        } catch (Exception ex) {
            // Same defensive pattern as every other producer in this project -
            // approval already succeeded and was saved; a broker hiccup
            // should not roll that back, just skip the notification.
            log.error("Failed to publish event [{}] for {}: {}", routingKey, companyName, ex.getMessage());
        }
    }
}
