package com.ecotrack.user.constant;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    /** Same shared exchange every EcoTrack microservice publishes business events to. */
    public static final String EXCHANGE_NAME = "ecotrack.events";

    public static final String ROUTING_KEY_RECYCLER_APPROVED = "recycler.approved";
    public static final String ROUTING_KEY_INDUSTRY_APPROVED = "industry.approved";
}
