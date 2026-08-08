package com.ecotrack.user.service.impl;

import com.ecotrack.user.repository.IndustryApplicationRepository;
import com.ecotrack.user.repository.IndustryRepository;
import com.ecotrack.user.repository.RecyclerApplicationRepository;
import com.ecotrack.user.repository.RecyclerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateCityServiceImplTest {

    @Mock
    private RecyclerApplicationRepository recyclerApplicationRepository;

    @Mock
    private IndustryApplicationRepository industryApplicationRepository;

    @Mock
    private RecyclerRepository recyclerRepository;

    @Mock
    private IndustryRepository industryRepository;

    @InjectMocks
    private StateCityServiceImpl stateCityService;

    @Test
    @DisplayName("Should return true when GST is already registered in any application or profile table")
    void testIsGstNumberAlreadyRegistered() {
        String gst = "27ABCDE1234F1Z5";
        when(recyclerApplicationRepository.existsByRegistrationNumberIgnoreCase(gst)).thenReturn(true);

        assertTrue(stateCityService.isGstNumberAlreadyRegistered(gst));
        assertFalse(stateCityService.isGstNumberAlreadyRegistered("27XYZAB9999F1Z0"));
    }

    @Test
    @DisplayName("Should validate valid state and dependent city pairs")
    void testIsValidStateAndCity() {
        assertTrue(stateCityService.isValidStateAndCity("Maharashtra", "Pune"));
        assertTrue(stateCityService.isValidStateAndCity("Rajasthan", "Jaipur"));
        assertFalse(stateCityService.isValidStateAndCity("Maharashtra", "Jaipur")); // Jaipur is in Rajasthan
        assertFalse(stateCityService.isValidStateAndCity("UnknownState", "Pune"));
    }

    @Test
    @DisplayName("Should fetch all states and cities correctly")
    void testGetStatesAndCities() {
        List<String> states = stateCityService.getAllStates();
        assertTrue(states.contains("Maharashtra"));
        assertTrue(states.contains("Rajasthan"));

        List<String> maharashtraCities = stateCityService.getCitiesByState("Maharashtra");
        assertTrue(maharashtraCities.contains("Mumbai"));
        assertTrue(maharashtraCities.contains("Pune"));
    }
}
