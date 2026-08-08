package com.ecotrack.user.service.impl;

import com.ecotrack.user.dto.response.IndustryResponse;
import com.ecotrack.user.entity.Industry;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.mapper.UserMapper;
import com.ecotrack.user.repository.IndustryRepository;
import com.ecotrack.user.service.IndustryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository industryRepository;

    @Override
    public IndustryResponse getByUserId(Long userId) {
        Industry industry = industryRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry profile not found"));
        return UserMapper.toResponse(industry);
    }

    @Override
    public IndustryResponse getById(Long id) {
        Industry industry = industryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found with id: " + id));
        return UserMapper.toResponse(industry);
    }

    @Override
    public Page<IndustryResponse> search(String companyName, Pageable pageable) {
        Page<Industry> industries = (companyName != null && !companyName.isBlank())
                ? industryRepository.findByCompanyNameContainingIgnoreCase(companyName, pageable)
                : industryRepository.findAll(pageable);
        return industries.map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public IndustryResponse suspend(Long id, boolean suspend) {
        Industry industry = industryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found with id: " + id));
        industry.setSuspended(suspend);
        industry = industryRepository.save(industry);
        log.info("Industry {} {}", id, suspend ? "suspended" : "reactivated");
        return UserMapper.toResponse(industry);
    }
}
