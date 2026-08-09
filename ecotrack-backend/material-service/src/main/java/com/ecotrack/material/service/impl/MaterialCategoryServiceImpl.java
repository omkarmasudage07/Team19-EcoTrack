package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.MaterialCategoryRequest;
import com.ecotrack.material.dto.response.MaterialCategoryResponse;
import com.ecotrack.material.entity.MaterialCategory;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.mapper.MaterialMapper;
import com.ecotrack.material.repository.MaterialCategoryRepository;
import com.ecotrack.material.service.MaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MaterialCategoryResponse create(MaterialCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("A material category with this name already exists", HttpStatus.CONFLICT);
        }
        MaterialCategory category = MaterialCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
        category = categoryRepository.save(category);
        return MaterialMapper.toResponse(category);
    }

    @Override
    public List<MaterialCategoryResponse> getAllActive() {
        return categoryRepository.findByActiveTrue().stream()
                .map(MaterialMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialCategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(MaterialMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaterialCategoryResponse setActive(Long id, boolean active) {
        MaterialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material category not found with id: " + id));
        category.setActive(active);
        category = categoryRepository.save(category);
        return MaterialMapper.toResponse(category);
    }
}
