package com.ecotrack.user.security;

import lombok.Getter;

/**
 * Unlike Auth Service, this service has no local `users` table to look
 * up - it trusts the JWT itself (issued and signed by Auth Service) and
 * builds this lightweight principal directly from its claims.
 */
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
