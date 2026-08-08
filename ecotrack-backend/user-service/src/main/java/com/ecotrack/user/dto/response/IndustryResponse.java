package com.ecotrack.user.dto.response;

import com.ecotrack.user.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndustryResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String companyRegistrationNumber;
    private String contactPerson;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private ApprovalStatus approvalStatus;
    private boolean suspended;
    private LocalDateTime createdAt;
}
