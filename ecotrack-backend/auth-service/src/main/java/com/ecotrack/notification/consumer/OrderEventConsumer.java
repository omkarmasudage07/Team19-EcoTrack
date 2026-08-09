package com.ecotrack.notification.consumer;

import com.ecotrack.notification.constant.RabbitMQConstants;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.event.OrderEvent;
import com.ecotrack.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_ORDER_PLACED)
    public void onOrderPlaced(OrderEvent event) {
        notificationService.notify(
                event.getRecyclerId(), RoleType.RECYCLER,
                "New Order Received",
                "You received a new order " + event.getOrderNumber() + " worth " + event.getTotalAmount() + ".",
                NotificationType.ORDER_PLACED);

        notificationService.logAudit("ORDER_PLACED", "Order " + event.getOrderNumber() + " placed");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PAYMENT_SUCCESSFUL)
    public void onPaymentSuccessful(OrderEvent event) {
        notificationService.notify(
                event.getIndustryId(), RoleType.INDUSTRY,
                "Payment Successful",
                "Payment for order " + event.getOrderNumber() + " was successful.",
                NotificationType.PAYMENT_SUCCESSFUL);

        notificationService.notify(
                event.getRecyclerId(), RoleType.RECYCLER,
                "Payment Received",
                "Payment for order " + event.getOrderNumber() + " has been received. You can start processing it.",
                NotificationType.PAYMENT_SUCCESSFUL);

        notificationService.logAudit("PAYMENT_SUCCESSFUL", "Order " + event.getOrderNumber() + " paid");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_ORDER_SHIPPED)
    public void onOrderShipped(OrderEvent event) {
        notificationService.notify(
                event.getIndustryId(), RoleType.INDUSTRY,
                "Order Shipped",
                "Your order " + event.getOrderNumber() + " has been shipped.",
                NotificationType.ORDER_SHIPPED);

        notificationService.logAudit("ORDER_SHIPPED", "Order " + event.getOrderNumber() + " shipped");
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_ORDER_DELIVERED)
    public void onOrderDelivered(OrderEvent event) {
        notificationService.notify(
                event.getIndustryId(), RoleType.INDUSTRY,
                "Order Delivered",
                "Your order " + event.getOrderNumber() + " has been delivered.",
                NotificationType.ORDER_DELIVERED);

        notificationService.logAudit("ORDER_DELIVERED", "Order " + event.getOrderNumber() + " delivered");
    }
}
