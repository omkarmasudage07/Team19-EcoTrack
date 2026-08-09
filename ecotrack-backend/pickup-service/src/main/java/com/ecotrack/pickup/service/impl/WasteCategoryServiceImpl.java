package com.ecotrack.pickup.service.impl;

import com.ecotrack.pickup.dto.request.WasteCategoryRequest;
import com.ecotrack.pickup.dto.response.WasteCategoryResponse;
import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.exception.BusinessException;
import com.ecotrack.pickup.exception.ResourceNotFoundException;
import com.ecotrack.pickup.mapper.PickupMapper;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import com.ecotrack.pickup.service.WasteCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteCategoryServiceImpl implements WasteCategoryService {

    private final WasteCategoryRepository wasteCategoryRepository;

    @Override
    @Transactional
    public WasteCategoryResponse create(WasteCategoryRequest request) {
        if (wasteCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("A waste category with this name already exists", HttpStatus.CONFLICT);
        }

        WasteCategory category = WasteCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();

        category = wasteCategoryRepository.save(category);
        return PickupMapper.toResponse(category);
    }

    @Override
    public List<WasteCategoryResponse> getAllActive() {
        return wasteCategoryRepository.findByActiveTrue().stream()
                .map(PickupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WasteCategoryResponse> getAll() {
        return wasteCategoryRepository.findAll().stream()
                .map(PickupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WasteCategoryResponse setActive(Long id, boolean active) {
        WasteCategory category = wasteCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste category not found with id: " + id));
        category.setActive(active);
        category = wasteCategoryRepository.save(category);
        return PickupMapper.toResponse(category);
    }
}
