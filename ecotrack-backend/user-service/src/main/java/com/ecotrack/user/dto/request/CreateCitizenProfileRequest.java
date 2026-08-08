package com.ecotrack.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sent by the Auth Service, via Feign, immediately after a new Citizen
 * account is created, so the profile row exists from the very first
 * login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCitizenProfileRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    private String phone;
}
