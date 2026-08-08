package com.ecotrack.user.service;

import java.util.List;
import java.util.Map;

public interface StateCityService {

    List<String> getAllStates();

    List<String> getCitiesByState(String stateName);

    boolean isValidStateAndCity(String stateName, String cityName);

    boolean isGstNumberAlreadyRegistered(String gstNumber);
}
