package com.ecotrack.user.service;

import com.ecotrack.user.dto.request.CitizenProfileUpdateRequest;
import com.ecotrack.user.dto.request.CreateCitizenProfileRequest;
import com.ecotrack.user.dto.response.CitizenResponse;

public interface CitizenService {

    CitizenResponse createProfile(CreateCitizenProfileRequest request);

    CitizenResponse getByUserId(Long userId);

    CitizenResponse updateProfile(Long userId, CitizenProfileUpdateRequest request);

    CitizenResponse updateProfilePhoto(Long userId, String photoUrl);
}
