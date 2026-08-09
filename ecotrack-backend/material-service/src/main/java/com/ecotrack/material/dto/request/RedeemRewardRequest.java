package com.ecotrack.material.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeemRewardRequest {
    private String deliveryAddress;
    private String contactPhone;
}
