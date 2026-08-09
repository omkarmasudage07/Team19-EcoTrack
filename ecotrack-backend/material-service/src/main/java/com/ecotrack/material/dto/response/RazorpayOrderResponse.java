package com.ecotrack.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Everything the frontend needs to open Razorpay's Checkout widget.
 * `keyId` is Razorpay's public key - safe to send to the browser (that is
 * exactly what it's for). The secret key never leaves the backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayOrderResponse {
    private Long ecotrackOrderId;
    private String razorpayOrderId;
    private String keyId;
    private long amountInPaise;
    private String currency;
}
