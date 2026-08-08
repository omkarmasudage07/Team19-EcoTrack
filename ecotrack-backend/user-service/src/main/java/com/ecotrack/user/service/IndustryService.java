package com.ecotrack.user.service;

import com.ecotrack.user.dto.response.IndustryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndustryService {

    IndustryResponse getByUserId(Long userId);

    IndustryResponse getById(Long id);

    Page<IndustryResponse> search(String companyName, Pageable pageable);

    IndustryResponse suspend(Long id, boolean suspend);
}
