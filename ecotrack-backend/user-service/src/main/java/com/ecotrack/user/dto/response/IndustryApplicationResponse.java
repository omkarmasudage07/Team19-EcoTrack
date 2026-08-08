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
public class IndustryApplicationResponse {
    private Long id;
    private String companyName;
    private String registrationNumber;
    private String email;
    private String contactPerson;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String documents;
    private ApprovalStatus status;
    private String remarks;
    private LocalDateTime submittedDate;
    private LocalDateTime reviewedDate;
}
