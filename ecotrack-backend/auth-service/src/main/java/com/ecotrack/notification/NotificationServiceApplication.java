package com.ecotrack.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * EcoTrack Notification Service.
 *
 * Listens to every business event published on the shared RabbitMQ
 * exchange (pickup accepted/completed/cancelled, order placed, payment
 * successful, order shipped/delivered) and turns each one into an
 * in-app notification for the right user. Also keeps a simple audit log
 * of these events for the Admin.
 *
 * Per project instruction, this service uses MySQL (not MongoDB, which
 * the original spec suggested) - same relational approach as every other
 * microservice in this project.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
