package com.ecotrack.material;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * EcoTrack Material Exchange Service.
 *
 * Owns the B2B marketplace: recovered material listings, inventory,
 * orders, mock payments, and the Citizen EcoPoints wallet. This is the
 * only service that touches money and points - Pickup Service only
 * announces that a pickup finished; this service decides how many points
 * that is worth and credits the wallet.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MaterialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialServiceApplication.class, args);
    }
}
