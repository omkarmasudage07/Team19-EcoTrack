package com.ecotrack.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EcoTrack Eureka Server.
 *
 * This is the Service Discovery component of the EcoTrack platform.
 * Every microservice (Auth, User, Pickup, Material, Notification) and the
 * API Gateway registers itself here on startup.
 *
 * Why we need it:
 * Instead of hardcoding URLs like http://localhost:8082 everywhere,
 * services simply register with a name (e.g. "user-service") and other
 * services look each other up by that name through Eureka. This makes it
 * possible to scale a service to multiple instances without changing any
 * configuration.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
