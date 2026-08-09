package com.ecotrack.material.client.dto;

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
    private boolean suspended;
}
