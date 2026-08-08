package com.ecotrack.pickup.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecyclerSummary {
    private Long id;
    private Long userId;
    private String companyName;
    private String phone;
    private String city;
    private boolean suspended;
}
