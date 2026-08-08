package com.ecotrack.pickup.constant;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    /** Single topic exchange every EcoTrack microservice publishes business events to. */
    public static final String EXCHANGE_NAME = "ecotrack.events";

    public static final String ROUTING_KEY_PICKUP_ACCEPTED = "pickup.accepted";
    public static final String ROUTING_KEY_PICKUP_COMPLETED = "pickup.completed";
    public static final String ROUTING_KEY_PICKUP_CANCELLED = "pickup.cancelled";
}
