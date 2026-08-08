package com.ecotrack.user.client.dto;

import com.ecotrack.user.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body sent to Auth Service's internal "create credentials" endpoint.
 * This is user-service's own copy of that contract - microservices do not
 * share DTO classes, they each keep a small local copy of the API shape
 * they depend on.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCredentialsRequest {
    private String email;
    private RoleType role;
}
