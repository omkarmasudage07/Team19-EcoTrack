package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.MockPaymentRequest;
import com.ecotrack.material.dto.request.RazorpayVerifyRequest;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.dto.response.RazorpayOrderResponse;

public interface PaymentService {

    /** [Industry] Completes mock payment for a PLACED order, confirming it. */
    OrderResponse processPayment(Long orderId, Long industryId, MockPaymentRequest request);

    /** [Industry] Step 1 of Razorpay checkout: creates a Razorpay order and a PENDING Payment row. */
    RazorpayOrderResponse createRazorpayOrder(Long orderId, Long industryId);

    /** [Industry] Step 2 of Razorpay checkout: verifies the signature Razorpay returned, then confirms the order. */
    OrderResponse verifyRazorpayPayment(Long orderId, Long industryId, RazorpayVerifyRequest request);
}
