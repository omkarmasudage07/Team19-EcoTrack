package com.ecotrack.pickup.service;

import com.ecotrack.pickup.dto.request.WasteCategoryRequest;
import com.ecotrack.pickup.dto.response.WasteCategoryResponse;

import java.util.List;

public interface WasteCategoryService {

    WasteCategoryResponse create(WasteCategoryRequest request);

    List<WasteCategoryResponse> getAllActive();

    List<WasteCategoryResponse> getAll();

    WasteCategoryResponse setActive(Long id, boolean active);
}
