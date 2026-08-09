package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.EcoPointRuleRequest;
import com.ecotrack.material.dto.response.EcoPointRuleResponse;
import com.ecotrack.material.entity.EcoPointRule;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.repository.EcoPointRuleRepository;
import com.ecotrack.material.service.EcoPointRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcoPointRuleServiceImpl implements EcoPointRuleService {

    private final EcoPointRuleRepository ruleRepository;

    @Override
    public List<EcoPointRuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EcoPointRuleResponse getRuleById(Long id) {
        EcoPointRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EcoPoint Rule not found with id: " + id));
        return toResponse(rule);
    }

    @Override
    public EcoPointRuleResponse getRuleByCategory(String categoryName) {
        EcoPointRule rule = ruleRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("EcoPoint Rule not found for category: " + categoryName));
        return toResponse(rule);
    }

    @Override
    @Transactional
    public EcoPointRuleResponse createRule(EcoPointRuleRequest request) {
        ruleRepository.findByCategoryNameIgnoreCase(request.getCategoryName())
                .ifPresent(existing -> {
                    throw new BusinessException("EcoPoint Rule for category '" + request.getCategoryName() + "' already exists", HttpStatus.CONFLICT);
                });

        EcoPointRule rule = EcoPointRule.builder()
                .categoryName(request.getCategoryName().trim())
                .pointsPerUnit(request.getPointsPerUnit())
                .ruleType(request.getRuleType() != null ? request.getRuleType() : "FLAT")
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        rule = ruleRepository.save(rule);
        log.info("Created EcoPoint Rule for category {} with {} points", rule.getCategoryName(), rule.getPointsPerUnit());
        return toResponse(rule);
    }

    @Override
    @Transactional
    public EcoPointRuleResponse updateRule(Long id, EcoPointRuleRequest request) {
        EcoPointRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EcoPoint Rule not found with id: " + id));

        rule.setPointsPerUnit(request.getPointsPerUnit());
        if (request.getRuleType() != null) rule.setRuleType(request.getRuleType());
        if (request.getDescription() != null) rule.setDescription(request.getDescription());
        if (request.getActive() != null) rule.setActive(request.getActive());

        rule = ruleRepository.save(rule);
        log.info("Updated EcoPoint Rule id {} for category {}: {} points", id, rule.getCategoryName(), rule.getPointsPerUnit());
        return toResponse(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        EcoPointRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EcoPoint Rule not found with id: " + id));
        ruleRepository.delete(rule);
        log.info("Deleted EcoPoint Rule id {}", id);
    }

    @Override
    public int calculatePointsForCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return 50; // default fallback points
        }
        return ruleRepository.findByCategoryNameIgnoreCase(categoryName.trim())
                .filter(EcoPointRule::isActive)
                .map(EcoPointRule::getPointsPerUnit)
                .orElse(50);
    }

    private EcoPointRuleResponse toResponse(EcoPointRule rule) {
        return EcoPointRuleResponse.builder()
                .id(rule.getId())
                .categoryName(rule.getCategoryName())
                .pointsPerUnit(rule.getPointsPerUnit())
                .ruleType(rule.getRuleType())
                .description(rule.getDescription())
                .active(rule.isActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
