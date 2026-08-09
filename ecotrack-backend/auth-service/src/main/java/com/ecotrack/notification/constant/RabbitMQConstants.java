package com.ecotrack.notification.constant;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String EXCHANGE_NAME = "ecotrack.events";

    // Routing keys this service listens to (published by Pickup and Material services)
    public static final String ROUTING_KEY_PICKUP_ACCEPTED = "pickup.accepted";
    public static final String ROUTING_KEY_PICKUP_COMPLETED = "pickup.completed";
    public static final String ROUTING_KEY_PICKUP_CANCELLED = "pickup.cancelled";
    public static final String ROUTING_KEY_ORDER_PLACED = "order.placed";
    public static final String ROUTING_KEY_PAYMENT_SUCCESSFUL = "payment.successful";
    public static final String ROUTING_KEY_ORDER_SHIPPED = "order.shipped";
    public static final String ROUTING_KEY_ORDER_DELIVERED = "order.delivered";
    public static final String ROUTING_KEY_RECYCLER_APPROVED = "recycler.approved";
    public static final String ROUTING_KEY_INDUSTRY_APPROVED = "industry.approved";
    public static final String ROUTING_KEY_REWARD_REDEEMED = "reward.redeemed";

    // This service's own queues - each bound to one routing key above
    public static final String QUEUE_PICKUP_ACCEPTED = "notification-service.pickup.accepted";
    public static final String QUEUE_PICKUP_COMPLETED = "notification-service.pickup.completed";
    public static final String QUEUE_PICKUP_CANCELLED = "notification-service.pickup.cancelled";
    public static final String QUEUE_ORDER_PLACED = "notification-service.order.placed";
    public static final String QUEUE_PAYMENT_SUCCESSFUL = "notification-service.payment.successful";
    public static final String QUEUE_ORDER_SHIPPED = "notification-service.order.shipped";
    public static final String QUEUE_ORDER_DELIVERED = "notification-service.order.delivered";
    public static final String QUEUE_RECYCLER_APPROVED = "notification-service.recycler.approved";
    public static final String QUEUE_INDUSTRY_APPROVED = "notification-service.industry.approved";
    public static final String QUEUE_REWARD_REDEEMED = "notification-service.reward.redeemed";
}
