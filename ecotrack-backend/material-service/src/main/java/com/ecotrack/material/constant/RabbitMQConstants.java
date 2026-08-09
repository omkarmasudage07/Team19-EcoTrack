package com.ecotrack.material.constant;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String EXCHANGE_NAME = "ecotrack.events";

    // Consumed - published by Pickup Service
    public static final String ROUTING_KEY_PICKUP_COMPLETED = "pickup.completed";
    public static final String QUEUE_MATERIAL_PICKUP_COMPLETED = "material-service.pickup.completed";

    // Published by this service
    public static final String ROUTING_KEY_ORDER_PLACED = "order.placed";
    public static final String ROUTING_KEY_PAYMENT_SUCCESSFUL = "payment.successful";
    public static final String ROUTING_KEY_ORDER_SHIPPED = "order.shipped";
    public static final String ROUTING_KEY_ORDER_DELIVERED = "order.delivered";
    public static final String ROUTING_KEY_MATERIAL_LISTED = "material.listed";
    public static final String ROUTING_KEY_ECOPOINTS_AWARDED = "ecopoints.awarded";
    public static final String ROUTING_KEY_REWARD_REDEEMED = "reward.redeemed";

    /** Flat EcoPoints awarded per completed pickup - simple and easy to explain in a viva. */
    public static final int ECOPOINTS_PER_COMPLETED_PICKUP = 50;
}
