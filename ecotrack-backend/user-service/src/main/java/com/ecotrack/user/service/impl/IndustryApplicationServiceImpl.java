package com.ecotrack.user.service.impl;

import com.ecotrack.user.client.AuthServiceClient;
import com.ecotrack.user.client.dto.CreateCredentialsRequest;
import com.ecotrack.user.client.dto.CredentialsResponse;
import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.IndustryApplicationRequest;
import com.ecotrack.user.dto.response.IndustryApplicationResponse;
import com.ecotrack.user.entity.Industry;
import com.ecotrack.user.entity.IndustryApplication;
import com.ecotrack.user.enums.ApprovalStatus;
import com.ecotrack.user.enums.RoleType;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.exception.ResourceNotFoundException;
import com.ecotrack.user.mapper.UserMapper;
import com.ecotrack.user.producer.ApplicationEventProducer;
import com.ecotrack.user.repository.IndustryApplicationRepository;
import com.ecotrack.user.repository.IndustryRepository;
import com.ecotrack.user.service.IndustryApplicationService;
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
public class IndustryApplicationServiceImpl implements IndustryApplicationService {

    private final IndustryApplicationRepository applicationRepository;
    private final IndustryRepository industryRepository;
    private final AuthServiceClient authServiceClient;
    private final ApplicationEventProducer applicationEventProducer;
    private final StateCityService stateCityService;

    @Override
    @Transactional
    public IndustryApplicationResponse submitApplication(IndustryApplicationRequest request) {
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

        IndustryApplication application = IndustryApplication.builder()
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
        log.info("Industry verification application submitted: {} ({})", application.getCompanyName(), application.getEmail());
        return UserMapper.toResponse(application);
    }

    @Override
    public Page<IndustryApplicationResponse> getApplications(ApprovalStatus status, Pageable pageable) {
        Page<IndustryApplication> applications = (status != null)
                ? applicationRepository.findByStatus(status, pageable)
                : applicationRepository.findAll(pageable);
        return applications.map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public IndustryApplicationResponse reviewApplication(Long applicationId, Long adminUserId, ApplicationReviewRequest request) {
        IndustryApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry application not found"));

        if (application.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("This application has already been reviewed", HttpStatus.CONFLICT);
        }

        application.setRemarks(request.getRemarks());
        application.setReviewedDate(LocalDateTime.now());
        application.setReviewedBy(adminUserId);

        if (Boolean.TRUE.equals(request.getApprove())) {
            application.setStatus(ApprovalStatus.APPROVED);

            var responseWrapper = authServiceClient.createCredentials(
                    new CreateCredentialsRequest(application.getEmail(), RoleType.INDUSTRY));
            if (responseWrapper == null || responseWrapper.getData() == null) {
                throw new BusinessException("Failed to obtain credentials from Auth Service", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            CredentialsResponse credentials = responseWrapper.getData();

            Industry industry = industryRepository.findByUserId(credentials.getUserId())
                    .orElseGet(() -> Industry.builder().userId(credentials.getUserId()).build());

            industry.setCompanyName(application.getCompanyName());
            industry.setCompanyRegistrationNumber(application.getRegistrationNumber());
            industry.setContactPerson(application.getContactPerson());
            industry.setPhone(application.getPhone());
            industry.setAddress(application.getAddress());
            industry.setCity(application.getCity());
            industry.setState(application.getState());
            industry.setPincode(application.getPincode());
            industry.setApprovalStatus(ApprovalStatus.APPROVED);
            industry.setApprovedBy(adminUserId);
            industry.setApprovedDate(LocalDateTime.now());

            industryRepository.save(industry);

            applicationEventProducer.publishIndustryApproved(
                    credentials.getUserId(), application.getCompanyName(), application.getEmail());

            log.info("Industry application {} approved. Temporary password for {}: {}",
                    applicationId, credentials.getEmail(), credentials.getTemporaryPassword());
        } else {
            application.setStatus(ApprovalStatus.REJECTED);
            log.info("Industry application {} rejected", applicationId);
        }

        application = applicationRepository.save(application);
        return UserMapper.toResponse(application);
    }
}
