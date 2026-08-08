package com.ecotrack.user.service.impl;

import com.ecotrack.user.repository.IndustryApplicationRepository;
import com.ecotrack.user.repository.IndustryRepository;
import com.ecotrack.user.repository.RecyclerApplicationRepository;
import com.ecotrack.user.repository.RecyclerRepository;
import com.ecotrack.user.service.StateCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StateCityServiceImpl implements StateCityService {

    private final RecyclerApplicationRepository recyclerApplicationRepository;
    private final IndustryApplicationRepository industryApplicationRepository;
    private final RecyclerRepository recyclerRepository;
    private final IndustryRepository industryRepository;

    private static final Map<String, List<String>> STATE_CITY_MAP = new LinkedHashMap<>();

    static {
        STATE_CITY_MAP.put("Maharashtra", List.of("Mumbai", "Pune", "Kolhapur", "Satara", "Nashik", "Nagpur", "Thane", "Solapur", "Aurangabad", "Amravati"));
        STATE_CITY_MAP.put("Rajasthan", List.of("Jaipur", "Jodhpur", "Udaipur", "Ajmer", "Kota", "Bikaner", "Bhilwara", "Alwar"));
        STATE_CITY_MAP.put("Gujarat", List.of("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar", "Jamnagar", "Gandhinagar"));
        STATE_CITY_MAP.put("Karnataka", List.of("Bengaluru", "Mysuru", "Hubballi", "Mangaluru", "Belagavi", "Davanagere", "Ballari"));
        STATE_CITY_MAP.put("Delhi", List.of("New Delhi", "North Delhi", "South Delhi", "West Delhi", "Central Delhi", "East Delhi"));
        STATE_CITY_MAP.put("Tamil Nadu", List.of("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Vellore"));
        STATE_CITY_MAP.put("Uttar Pradesh", List.of("Lucknow", "Kanpur", "Agra", "Varanasi", "Noida", "Ghaziabad", "Meerut", "Prayagraj"));
        STATE_CITY_MAP.put("West Bengal", List.of("Kolkata", "Howrah", "Durgapur", "Siliguri", "Asansol", "Bardhaman"));
        STATE_CITY_MAP.put("Telangana", List.of("Hyderabad", "Warangal", "Nizamabad", "Karimnagar", "Khammam"));
        STATE_CITY_MAP.put("Madhya Pradesh", List.of("Bhopal", "Indore", "Gwalior", "Jabalpur", "Ujjain", "Sagar"));
        STATE_CITY_MAP.put("Punjab", List.of("Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Bathinda"));
        STATE_CITY_MAP.put("Haryana", List.of("Gurugram", "Faridabad", "Panipat", "Ambala", "Karnal"));
        STATE_CITY_MAP.put("Kerala", List.of("Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur", "Kollam"));
        STATE_CITY_MAP.put("Bihar", List.of("Patna", "Gaya", "Bhagalpur", "Muzaffarpur", "Purnia"));
        STATE_CITY_MAP.put("Goa", List.of("Panaji", "Margao", "Vasco da Gama", "Mapusa"));
    }

    @Override
    public List<String> getAllStates() {
        return new ArrayList<>(STATE_CITY_MAP.keySet());
    }

    @Override
    public List<String> getCitiesByState(String stateName) {
        if (stateName == null) {
            return Collections.emptyList();
        }
        return STATE_CITY_MAP.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(stateName.trim()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean isValidStateAndCity(String stateName, String cityName) {
        if (stateName == null || cityName == null) {
            return false;
        }
        List<String> cities = getCitiesByState(stateName);
        return cities.stream().anyMatch(c -> c.equalsIgnoreCase(cityName.trim()));
    }

    @Override
    public boolean isGstNumberAlreadyRegistered(String gstNumber) {
        if (gstNumber == null || gstNumber.isBlank()) {
            return false;
        }
        String formattedGst = gstNumber.trim();
        return recyclerApplicationRepository.existsByRegistrationNumberIgnoreCase(formattedGst)
                || industryApplicationRepository.existsByRegistrationNumberIgnoreCase(formattedGst)
                || recyclerRepository.existsByCompanyRegistrationNumberIgnoreCase(formattedGst)
                || industryRepository.existsByCompanyRegistrationNumberIgnoreCase(formattedGst);
    }
}
