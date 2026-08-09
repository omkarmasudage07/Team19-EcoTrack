package com.ecotrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationApprovedEvent implements Serializable {
    private Long userId;
    private String companyName;
    private String email;
    private LocalDateTime occurredAt;
}
