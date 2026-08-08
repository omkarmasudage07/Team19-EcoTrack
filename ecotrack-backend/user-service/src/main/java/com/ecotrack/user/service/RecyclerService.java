package com.ecotrack.user.service;

import com.ecotrack.user.dto.response.RecyclerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecyclerService {

    RecyclerResponse getByUserId(Long userId);

    RecyclerResponse getById(Long id);

    Page<RecyclerResponse> search(String companyName, Pageable pageable);

    RecyclerResponse suspend(Long id, boolean suspend);
}
