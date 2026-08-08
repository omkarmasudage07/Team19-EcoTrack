package com.ecotrack.user.service;

import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.RecyclerApplicationRequest;
import com.ecotrack.user.dto.response.RecyclerApplicationResponse;
import com.ecotrack.user.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecyclerApplicationService {

    RecyclerApplicationResponse submitApplication(RecyclerApplicationRequest request);

    Page<RecyclerApplicationResponse> getApplications(ApprovalStatus status, Pageable pageable);

    RecyclerApplicationResponse reviewApplication(Long applicationId, Long adminUserId, ApplicationReviewRequest request);
}
