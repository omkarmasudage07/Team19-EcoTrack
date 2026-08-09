package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.MaterialCategoryRequest;
import com.ecotrack.material.dto.response.MaterialCategoryResponse;

import java.util.List;

public interface MaterialCategoryService {

    MaterialCategoryResponse create(MaterialCategoryRequest request);

    List<MaterialCategoryResponse> getAllActive();

    List<MaterialCategoryResponse> getAll();

    MaterialCategoryResponse setActive(Long id, boolean active);
}
