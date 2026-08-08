package com.ecotrack.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * EcoTrack User Service.
 *
 * Owns all profile data: Citizen, Recycler and Industry profiles, and the
 * partnership/verification application workflow that Recyclers and
 * Industries must go through before they get login credentials.
 *
 * This service never issues JWT tokens - it asks the Auth Service (via
 * OpenFeign) to do that once an Admin approves an application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
