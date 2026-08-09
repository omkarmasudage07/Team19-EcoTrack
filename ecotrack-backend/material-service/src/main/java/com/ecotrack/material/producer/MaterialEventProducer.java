package com.ecotrack.material.producer;

import com.ecotrack.material.constant.RabbitMQConstants;
import com.ecotrack.material.entity.Order;
import com.ecotrack.material.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderPlaced(Order order) {
        publish(RabbitMQConstants.ROUTING_KEY_ORDER_PLACED, order);
    }

    public void publishPaymentSuccessful(Order order) {
        publish(RabbitMQConstants.ROUTING_KEY_PAYMENT_SUCCESSFUL, order);
    }

    public void publishOrderShipped(Order order) {
        publish(RabbitMQConstants.ROUTING_KEY_ORDER_SHIPPED, order);
    }

    public void publishOrderDelivered(Order order) {
        publish(RabbitMQConstants.ROUTING_KEY_ORDER_DELIVERED, order);
    }

    private void publish(String routingKey, Order order) {
        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .industryId(order.getIndustryId())
                .recyclerId(order.getRecyclerId())
                .status(order.getOrderStatus().name())
                .totalAmount(order.getTotalAmount())
                .occurredAt(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, routingKey, event);
            log.info("Published event [{}] for order {}", routingKey, order.getOrderNumber());
        } catch (Exception ex) {
            log.error("Failed to publish event [{}] for order {}: {}", routingKey, order.getOrderNumber(), ex.getMessage());
        }
    }
}
