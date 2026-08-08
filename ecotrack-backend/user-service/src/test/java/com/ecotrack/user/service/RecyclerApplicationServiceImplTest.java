package com.ecotrack.user.service;

import com.ecotrack.user.client.AuthServiceClient;
import com.ecotrack.user.client.dto.ApiResponse;
import com.ecotrack.user.client.dto.CredentialsResponse;
import com.ecotrack.user.dto.request.ApplicationReviewRequest;
import com.ecotrack.user.dto.request.RecyclerApplicationRequest;
import com.ecotrack.user.entity.RecyclerApplication;
import com.ecotrack.user.enums.ApprovalStatus;
import com.ecotrack.user.enums.RoleType;
import com.ecotrack.user.exception.BusinessException;
import com.ecotrack.user.repository.RecyclerApplicationRepository;
import com.ecotrack.user.repository.RecyclerRepository;
import com.ecotrack.user.service.impl.RecyclerApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecotrack.user.producer.ApplicationEventProducer;

import com.ecotrack.user.service.StateCityService;

@ExtendWith(MockitoExtension.class)
class RecyclerApplicationServiceImplTest {

    @Mock
    private RecyclerApplicationRepository applicationRepository;
    @Mock
    private RecyclerRepository recyclerRepository;
    @Mock
    private AuthServiceClient authServiceClient;
    @Mock
    private ApplicationEventProducer applicationEventProducer;
    @Mock
    private StateCityService stateCityService;

    @InjectMocks
    private RecyclerApplicationServiceImpl applicationService;

    @Test
    void submitApplication_throwsWhenGstNumberAlreadyRegistered() {
        RecyclerApplicationRequest request = new RecyclerApplicationRequest(
                "Green Recyclers", "27ABCDE1234F1Z5", "green@example.com", "Ravi Kumar",
                "9876543210", "Industrial Area Plot 4", "Pune", "Maharashtra", "411001", null);

        when(stateCityService.isGstNumberAlreadyRegistered(request.getRegistrationNumber())).thenReturn(true);

        assertThatThrownBy(() -> applicationService.submitApplication(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("GST Number already registered.");
    }

    @Test
    void submitApplication_throwsWhenAPendingApplicationAlreadyExists() {
        RecyclerApplicationRequest request = new RecyclerApplicationRequest(
                "Green Recyclers", "27ABCDE1234F1Z5", "green@example.com", "Ravi Kumar",
                "9876543210", "Industrial Area Plot 4", "Pune", "Maharashtra", "411001", null);

        when(stateCityService.isGstNumberAlreadyRegistered(request.getRegistrationNumber())).thenReturn(false);
        when(applicationRepository.existsByEmailAndStatus(request.getEmail(), ApprovalStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> applicationService.submitApplication(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already pending review");
    }

    @Test
    void reviewApplication_approvingCreatesRecyclerAndRequestsCredentials() {
        RecyclerApplication application = RecyclerApplication.builder()
                .id(1L)
                .companyName("Green Recyclers")
                .registrationNumber("REG123")
                .email("[email protected]")
                .contactPerson("Ravi Kumar")
                .phone("9876543210")
                .status(ApprovalStatus.PENDING)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(RecyclerApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CredentialsResponse credentials = new CredentialsResponse(
                101L, "[email protected]", RoleType.RECYCLER, "Temp@1234");
        ApiResponse<CredentialsResponse> apiResponse = new ApiResponse<>(
                null, 201, "Created", credentials, null);
        when(authServiceClient.createCredentials(any())).thenReturn(apiResponse);

        ApplicationReviewRequest reviewRequest = new ApplicationReviewRequest(true, "Looks good");

        var result = applicationService.reviewApplication(1L, 999L, reviewRequest);

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(applicationRepository.save(application)).isNotNull();
    }

    @Test
    void reviewApplication_throwsWhenAlreadyReviewed() {
        RecyclerApplication application = RecyclerApplication.builder()
                .id(2L)
                .status(ApprovalStatus.APPROVED)
                .build();

        when(applicationRepository.findById(2L)).thenReturn(Optional.of(application));

        ApplicationReviewRequest reviewRequest = new ApplicationReviewRequest(true, null);

        assertThatThrownBy(() -> applicationService.reviewApplication(2L, 999L, reviewRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already been reviewed");
    }
}
