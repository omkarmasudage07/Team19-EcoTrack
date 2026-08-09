package com.ecotrack.material.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The three values Razorpay's Checkout widget hands back to the frontend
 * after a successful payment. We re-verify all three server-side using
 * the key secret before trusting the payment - never trust the frontend
 * alone for "did this payment succeed".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayVerifyRequest {

    @NotBlank(message = "razorpayOrderId is required")
    private String razorpayOrderId;

    @NotBlank(message = "razorpayPaymentId is required")
    private String razorpayPaymentId;

    @NotBlank(message = "razorpaySignature is required")
    private String razorpaySignature;
}
