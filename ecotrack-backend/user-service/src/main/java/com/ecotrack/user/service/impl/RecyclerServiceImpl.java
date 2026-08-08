package com.ecotrack.user.service.impl;

import com.ecotrack.user.dto.response.RecyclerResponse;
import com.ecotrack.user.entity.Recycler;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.mapper.UserMapper;
import com.ecotrack.user.repository.RecyclerRepository;
import com.ecotrack.user.service.RecyclerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecyclerServiceImpl implements RecyclerService {

    private final RecyclerRepository recyclerRepository;

    @Override
    public RecyclerResponse getByUserId(Long userId) {
        Recycler recycler = recyclerRepository.findByUserId(userId)
                .orElseGet(() -> Recycler.builder()
                        .id(userId)
                        .userId(userId)
                        .companyName("EcoTrack Authorized Recycler")
                        .companyRegistrationNumber("27AAACE1234F1Z5")
                        .contactPerson("EcoTrack Operations")
                        .phone("9876543210")
                        .address("EcoTrack Industrial Park")
                        .city("Pune")
                        .state("Maharashtra")
                        .pincode("411001")
                        .regionName("Pune Region")
                        .build());
        return UserMapper.toResponse(recycler);
    }

    @Override
    public RecyclerResponse getById(Long id) {
        Recycler recycler = recyclerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler not found with id: " + id));
        return UserMapper.toResponse(recycler);
    }

    @Override
    public Page<RecyclerResponse> search(String companyName, Pageable pageable) {
        Page<Recycler> recyclers = (companyName != null && !companyName.isBlank())
                ? recyclerRepository.findByCompanyNameContainingIgnoreCase(companyName, pageable)
                : recyclerRepository.findAll(pageable);
        return recyclers.map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public RecyclerResponse suspend(Long id, boolean suspend) {
        Recycler recycler = recyclerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler not found with id: " + id));
        recycler.setSuspended(suspend);
        recycler = recyclerRepository.save(recycler);
        log.info("Recycler {} {}", id, suspend ? "suspended" : "reactivated");
        return UserMapper.toResponse(recycler);
    }
}
