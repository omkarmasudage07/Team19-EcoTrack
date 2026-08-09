package com.ecotrack.material.security;

import lombok.Getter;

@Getter
public class AuthenticatedUser {

    private final Long userId;
    private final String email;
    private final String role;

    public AuthenticatedUser(Long userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}
