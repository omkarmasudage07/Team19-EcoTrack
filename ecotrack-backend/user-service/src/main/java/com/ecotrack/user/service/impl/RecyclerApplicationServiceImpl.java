package com.ecotrack.user.service.impl;

import com.ecotrack.user.client.AuthServiceClient;
import com.ecotrack.user.client.dto.CreateCredentialsRequest;
import com.ecotrack.user.client.dto.CredentialsResponse;
import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.RecyclerApplicationRequest;
import com.ecotrack.user.dto.response.RecyclerApplicationResponse;
import com.ecotrack.user.entity.Recycler;
import com.ecotrack.user.entity.RecyclerApplication;
import com.ecotrack.user.enums.ApprovalStatus;
import com.ecotrack.user.enums.RoleType;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.mapper.UserMapper;
import com.ecotrack.user.producer.ApplicationEventProducer;
import com.ecotrack.user.repository.RecyclerApplicationRepository;
import com.ecotrack.user.repository.RecyclerRepository;
import com.ecotrack.user.service.RecyclerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.ecotrack.user.service.StateCityService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecyclerApplicationServiceImpl implements RecyclerApplicationService {

    private final RecyclerApplicationRepository applicationRepository;
    private final RecyclerRepository recyclerRepository;
    private final AuthServiceClient authServiceClient;
    private final ApplicationEventProducer applicationEventProducer;
    private final StateCityService stateCityService;

    @Override
    @Transactional
    public RecyclerApplicationResponse submitApplication(RecyclerApplicationRequest request) {
        if (stateCityService.isGstNumberAlreadyRegistered(request.getRegistrationNumber())) {
            throw new BusinessException("GST Number already registered.", HttpStatus.CONFLICT);
        }

        if (applicationRepository.existsByEmailAndStatus(request.getEmail(), ApprovalStatus.PENDING)) {
            throw new BusinessException(
                    "An application with this email is already pending review", HttpStatus.CONFLICT);
        }

        if (!stateCityService.isValidStateAndCity(request.getState(), request.getCity())) {
            throw new BusinessException("Selected City does not belong to the selected State.", HttpStatus.BAD_REQUEST);
        }

        RecyclerApplication application = RecyclerApplication.builder()
                .companyName(request.getCompanyName())
                .registrationNumber(request.getRegistrationNumber())
                .email(request.getEmail())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .documents(request.getDocuments())
                .status(ApprovalStatus.PENDING)
                .build();

        application = applicationRepository.save(application);
        log.info("Recycler partnership application submitted: {} ({})", application.getCompanyName(), application.getEmail());
        return UserMapper.toResponse(application);
    }

    @Override
    public Page<RecyclerApplicationResponse> getApplications(ApprovalStatus status, Pageable pageable) {
        Page<RecyclerApplication> applications = (status != null)
                ? applicationRepository.findByStatus(status, pageable)
                : applicationRepository.findAll(pageable);
        return applications.map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public RecyclerApplicationResponse reviewApplication(Long applicationId, Long adminUserId, ApplicationReviewRequest request) {
        RecyclerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler application not found"));

        if (application.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("This application has already been reviewed", HttpStatus.CONFLICT);
        }

        application.setRemarks(request.getRemarks());
        application.setReviewedDate(LocalDateTime.now());
        application.setReviewedBy(adminUserId);

        if (Boolean.TRUE.equals(request.getApprove())) {
            application.setStatus(ApprovalStatus.APPROVED);

            var responseWrapper = authServiceClient.createCredentials(
                    new CreateCredentialsRequest(application.getEmail(), RoleType.RECYCLER));
            if (responseWrapper == null || responseWrapper.getData() == null) {
                throw new BusinessException("Failed to obtain credentials from Auth Service", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            CredentialsResponse credentials = responseWrapper.getData();

            Recycler recycler = recyclerRepository.findByUserId(credentials.getUserId())
                    .orElseGet(() -> Recycler.builder().userId(credentials.getUserId()).build());

            recycler.setCompanyName(application.getCompanyName());
            recycler.setCompanyRegistrationNumber(application.getRegistrationNumber());
            recycler.setContactPerson(application.getContactPerson());
            recycler.setPhone(application.getPhone());
            recycler.setAddress(application.getAddress());
            recycler.setCity(application.getCity());
            recycler.setState(application.getState());
            recycler.setPincode(application.getPincode());
            recycler.setApprovalStatus(ApprovalStatus.APPROVED);
            recycler.setApprovedBy(adminUserId);
            recycler.setApprovedDate(LocalDateTime.now());

            recyclerRepository.save(recycler);

            applicationEventProducer.publishRecyclerApproved(
                    credentials.getUserId(), application.getCompanyName(), application.getEmail());

            log.info("Recycler application {} approved. Temporary password for {}: {}",
                    applicationId, credentials.getEmail(), credentials.getTemporaryPassword());
        } else {
            application.setStatus(ApprovalStatus.REJECTED);
            log.info("Recycler application {} rejected", applicationId);
        }

        application = applicationRepository.save(application);
        return UserMapper.toResponse(application);
    }
}
