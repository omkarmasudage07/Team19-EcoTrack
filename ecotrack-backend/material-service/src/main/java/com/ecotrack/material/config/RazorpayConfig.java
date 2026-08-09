package com.ecotrack.material.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A single, shared Razorpay SDK client built from the test-mode key id
 * and key secret in application config. See material-service.yml for
 * where to paste your own free Razorpay test keys.
 */
@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(
            @Value("${razorpay.key-id:rzp_test_placeholder}") String keyId,
            @Value("${razorpay.key-secret:dummy_secret}") String keySecret) {
        try {
            return new RazorpayClient(keyId, keySecret);
        } catch (Exception e) {
            return null;
        }
    }
}
