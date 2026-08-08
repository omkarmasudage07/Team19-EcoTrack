package com.ecotrack.user.service.impl;

import com.ecotrack.user.dto.request.CitizenProfileUpdateRequest;
import com.ecotrack.user.dto.request.CreateCitizenProfileRequest;
import com.ecotrack.user.dto.response.CitizenResponse;
import com.ecotrack.user.entity.Citizen;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.mapper.UserMapper;
import com.ecotrack.user.repository.CitizenRepository;
import com.ecotrack.user.service.CitizenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;

    @Override
    @Transactional
    public CitizenResponse createProfile(CreateCitizenProfileRequest request) {
        if (citizenRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new BusinessException("Citizen profile already exists for this user", HttpStatus.CONFLICT);
        }

        Citizen citizen = Citizen.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .build();

        citizen = citizenRepository.save(citizen);
        log.info("Citizen profile created for userId: {}", request.getUserId());
        return UserMapper.toResponse(citizen);
    }

    @Override
    public CitizenResponse getByUserId(Long userId) {
        Citizen citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));
        return UserMapper.toResponse(citizen);
    }

    @Override
    @Transactional
    public CitizenResponse updateProfile(Long userId, CitizenProfileUpdateRequest request) {
        Citizen citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));

        citizen.setFullName(request.getFullName());
        citizen.setPhone(request.getPhone());
        citizen.setAddress(request.getAddress());
        citizen.setCity(request.getCity());
        citizen.setState(request.getState());
        citizen.setPincode(request.getPincode());

        citizen = citizenRepository.save(citizen);
        return UserMapper.toResponse(citizen);
    }

    @Override
    @Transactional
    public CitizenResponse updateProfilePhoto(Long userId, String photoUrl) {
        Citizen citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found"));
        citizen.setProfilePhoto(photoUrl);
        citizen = citizenRepository.save(citizen);
        return UserMapper.toResponse(citizen);
    }
}
