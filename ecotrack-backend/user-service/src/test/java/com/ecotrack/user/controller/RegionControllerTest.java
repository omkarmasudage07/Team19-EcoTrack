package com.ecotrack.user.controller;

import com.ecotrack.user.dto.request.RegionRequest;
import com.ecotrack.user.dto.response.RegionResponse;
import com.ecotrack.user.security.JwtAuthenticationFilter;
import com.ecotrack.user.security.JwtUtil;
import com.ecotrack.user.service.RegionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegionService regionService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("GET /api/v1/users/regions should return active regions")
    void testGetActiveRegionsEndpoint() throws Exception {
        RegionResponse response = RegionResponse.builder()
                .id(1L)
                .name("Pune Region")
                .code("PUNE")
                .active(true)
                .build();

        when(regionService.getActiveRegions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/users/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Pune Region"));
    }

    @Test
    @DisplayName("POST /api/v1/users/regions should create region")
    void testCreateRegionEndpoint() throws Exception {
        RegionRequest request = new RegionRequest("Satara Region", "SATARA", "Desc", true);
        RegionResponse response = RegionResponse.builder()
                .id(2L)
                .name("Satara Region")
                .code("SATARA")
                .active(true)
                .build();

        when(regionService.createRegion(any(RegionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Satara Region"));
    }
}
