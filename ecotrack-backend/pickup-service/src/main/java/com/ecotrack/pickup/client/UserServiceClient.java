package com.ecotrack.pickup.client;

import com.ecotrack.pickup.client.dto.ApiResponse;
import com.ecotrack.pickup.client.dto.CitizenSummary;
import com.ecotrack.pickup.client.dto.RecyclerSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/citizens/internal/{userId}")
    ApiResponse<CitizenSummary> getCitizenByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/recyclers/internal/{userId}")
    ApiResponse<RecyclerSummary> getRecyclerByUserId(@PathVariable("userId") Long userId);
}
