package com.ecotrack.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * EcoTrack Config Server.
 *
 * Holds the configuration (database URLs, JWT secret, RabbitMQ settings,
 * ports, gateway routes) for every microservice in one place, so we never
 * have to copy-paste the same properties into five different services.
 *
 * We use the "native" profile, which means configuration files are read
 * from this project's own classpath (src/main/resources/config) instead
 * of from a remote Git repository. This keeps local development simple
 * for a beginner-friendly setup while still teaching the centralized
 * configuration concept.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
