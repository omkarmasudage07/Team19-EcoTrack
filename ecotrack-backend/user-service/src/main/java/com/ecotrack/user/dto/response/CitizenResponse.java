package com.ecotrack.user.dto.response;

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
public class CitizenResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String profilePhoto;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDateTime createdAt;
}
