package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.EcoPointRuleRequest;
import com.ecotrack.material.dto.response.EcoPointRuleResponse;

import java.util.List;

public interface EcoPointRuleService {
    List<EcoPointRuleResponse> getAllRules();
    EcoPointRuleResponse getRuleById(Long id);
    EcoPointRuleResponse getRuleByCategory(String categoryName);
    EcoPointRuleResponse createRule(EcoPointRuleRequest request);
    EcoPointRuleResponse updateRule(Long id, EcoPointRuleRequest request);
    void deleteRule(Long id);
    int calculatePointsForCategory(String categoryName);
}
