package com.ecotrack.pickup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * EcoTrack Pickup Service.
 *
 * Owns the entire pickup lifecycle: a Citizen schedules a pickup, a
 * Recycler accepts and works it through to completion. This service
 * never stores Orders or Materials - that belongs to Material Exchange
 * Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PickupServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickupServiceApplication.class, args);
    }
}
