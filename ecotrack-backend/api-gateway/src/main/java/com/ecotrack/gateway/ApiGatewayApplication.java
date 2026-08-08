package com.ecotrack.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * EcoTrack API Gateway.
 *
 * This is the single entry point for the React frontend. The frontend
 * NEVER talks directly to a microservice - every request goes through
 * this Gateway first.
 *
 * Responsibilities:
 *  - Route each incoming request to the correct microservice (using the
 *    routes configured in the Config Server's api-gateway.yml).
 *  - Validate the JWT token on protected routes (see JwtAuthenticationFilter).
 *  - Apply a single, centralized CORS policy.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
