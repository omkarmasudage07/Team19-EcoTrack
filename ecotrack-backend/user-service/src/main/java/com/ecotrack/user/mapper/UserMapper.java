package com.ecotrack.user.mapper;

import com.ecotrack.user.dto.response.*;
import com.ecotrack.user.entity.*;

public final class UserMapper {

    private UserMapper() {
    }

    public static CitizenResponse toResponse(Citizen citizen) {
        return CitizenResponse.builder()
                .id(citizen.getId())
                .userId(citizen.getUserId())
                .fullName(citizen.getFullName())
                .phone(citizen.getPhone())
                .profilePhoto(citizen.getProfilePhoto())
                .address(citizen.getAddress())
                .city(citizen.getCity())
                .state(citizen.getState())
                .pincode(citizen.getPincode())
                .createdAt(citizen.getCreatedAt())
                .build();
    }

    public static RecyclerResponse toResponse(Recycler recycler) {
        return RecyclerResponse.builder()
                .id(recycler.getId())
                .userId(recycler.getUserId())
                .companyName(recycler.getCompanyName())
                .companyRegistrationNumber(recycler.getCompanyRegistrationNumber())
                .contactPerson(recycler.getContactPerson())
                .phone(recycler.getPhone())
                .address(recycler.getAddress())
                .city(recycler.getCity())
                .state(recycler.getState())
                .pincode(recycler.getPincode())
                .regionName(recycler.getRegionName())
                .approvalStatus(recycler.getApprovalStatus())
                .suspended(recycler.isSuspended())
                .createdAt(recycler.getCreatedAt())
                .build();
    }

    public static IndustryResponse toResponse(Industry industry) {
        return IndustryResponse.builder()
                .id(industry.getId())
                .userId(industry.getUserId())
                .companyName(industry.getCompanyName())
                .companyRegistrationNumber(industry.getCompanyRegistrationNumber())
                .contactPerson(industry.getContactPerson())
                .phone(industry.getPhone())
                .address(industry.getAddress())
                .city(industry.getCity())
                .state(industry.getState())
                .pincode(industry.getPincode())
                .approvalStatus(industry.getApprovalStatus())
                .suspended(industry.isSuspended())
                .createdAt(industry.getCreatedAt())
                .build();
    }

    public static RecyclerApplicationResponse toResponse(RecyclerApplication application) {
        return RecyclerApplicationResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .registrationNumber(application.getRegistrationNumber())
                .email(application.getEmail())
                .contactPerson(application.getContactPerson())
                .phone(application.getPhone())
                .address(application.getAddress())
                .city(application.getCity())
                .state(application.getState())
                .pincode(application.getPincode())
                .documents(application.getDocuments())
                .status(application.getStatus())
                .remarks(application.getRemarks())
                .submittedDate(application.getSubmittedDate())
                .reviewedDate(application.getReviewedDate())
                .build();
    }

    public static IndustryApplicationResponse toResponse(IndustryApplication application) {
        return IndustryApplicationResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .registrationNumber(application.getRegistrationNumber())
                .email(application.getEmail())
                .contactPerson(application.getContactPerson())
                .phone(application.getPhone())
                .address(application.getAddress())
                .city(application.getCity())
                .state(application.getState())
                .pincode(application.getPincode())
                .documents(application.getDocuments())
                .status(application.getStatus())
                .remarks(application.getRemarks())
                .submittedDate(application.getSubmittedDate())
                .reviewedDate(application.getReviewedDate())
                .build();
    }
}
