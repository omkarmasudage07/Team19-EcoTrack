package com.ecotrack.user.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Published the moment an Admin approves a Recycler or Industry
 * application. Notification Service listens for this to tell the
 * applicant their account is ready.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationApprovedEvent implements Serializable {
    private Long userId;
    private String companyName;
    private String email;
    private LocalDateTime occurredAt;
}
