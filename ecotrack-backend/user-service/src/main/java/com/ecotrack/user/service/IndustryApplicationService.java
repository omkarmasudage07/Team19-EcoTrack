package com.ecotrack.user.service;

import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.IndustryApplicationRequest;
import com.ecotrack.user.dto.response.IndustryApplicationResponse;
import com.ecotrack.user.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndustryApplicationService {

    IndustryApplicationResponse submitApplication(IndustryApplicationRequest request);

    Page<IndustryApplicationResponse> getApplications(ApprovalStatus status, Pageable pageable);

    IndustryApplicationResponse reviewApplication(Long applicationId, Long adminUserId, ApplicationReviewRequest request);
}
