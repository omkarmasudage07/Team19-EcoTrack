package com.ecotrack.user.client.dto;

import com.ecotrack.user.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsResponse {
    private Long userId;
    private String email;
    private RoleType role;
    private String temporaryPassword;
}
