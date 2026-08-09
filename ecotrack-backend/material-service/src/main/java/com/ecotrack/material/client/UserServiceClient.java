package com.ecotrack.material.client;

import com.ecotrack.material.client.dto.ApiResponse;
import com.ecotrack.material.client.dto.IndustrySummary;
import com.ecotrack.material.client.dto.RecyclerSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/recyclers/internal/{userId}")
    ApiResponse<RecyclerSummary> getRecyclerByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/industries/internal/{userId}")
    ApiResponse<IndustrySummary> getIndustryByUserId(@PathVariable("userId") Long userId);
}
