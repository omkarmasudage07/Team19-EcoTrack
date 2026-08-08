package com.ecotrack.user.client;

import com.ecotrack.user.client.dto.ApiResponse;
import com.ecotrack.user.client.dto.CreateCredentialsRequest;
import com.ecotrack.user.client.dto.CredentialsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Talks to the Auth Service using its Eureka-registered service name
 * ("auth-service") instead of a hardcoded host/port. Used only for the
 * internal, service-to-service "create credentials" call that happens
 * right after an Admin approves a Recycler or Industry application.
 */
@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/api/v1/auth/internal/create-credentials")
    ApiResponse<CredentialsResponse> createCredentials(@RequestBody CreateCredentialsRequest request);
}
