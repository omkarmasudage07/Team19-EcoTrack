package com.ecotrack.material.controller;

import com.ecotrack.material.dto.request.MockPaymentRequest;
import com.ecotrack.material.dto.request.PlaceOrderRequest;
import com.ecotrack.material.dto.request.RazorpayVerifyRequest;
import com.ecotrack.material.dto.request.UpdateOrderStatusRequest;
import com.ecotrack.material.dto.response.ApiResponse;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.dto.response.RazorpayOrderResponse;
import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.security.AuthenticatedUser;
import com.ecotrack.material.service.OrderService;
import com.ecotrack.material.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Industry purchase orders, mock payment, and Recycler fulfillment")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Operation(summary = "[Industry] Place a new order")
    @PreAuthorize("hasRole('INDUSTRY')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Order placed successfully", response));
    }

    @Operation(summary = "[Industry] Complete mock payment for an order")
    @PreAuthorize("hasRole('INDUSTRY')")
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<OrderResponse>> pay(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody MockPaymentRequest request) {
        OrderResponse response = paymentService.processPayment(id, user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment successful", response));
    }

    @Operation(summary = "[Industry] Create a Razorpay order - step 1 of Razorpay Test Mode checkout")
    @PreAuthorize("hasRole('INDUSTRY')")
    @PostMapping("/{id}/razorpay/create")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createRazorpayOrder(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        RazorpayOrderResponse response = paymentService.createRazorpayOrder(id, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Razorpay order created", response));
    }

    @Operation(summary = "[Industry] Verify a Razorpay payment - step 2, called after the checkout widget succeeds")
    @PreAuthorize("hasRole('INDUSTRY')")
    @PostMapping("/{id}/razorpay/verify")
    public ResponseEntity<ApiResponse<OrderResponse>> verifyRazorpayPayment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody RazorpayVerifyRequest request) {
        OrderResponse response = paymentService.verifyRazorpayPayment(id, user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment verified successfully", response));
    }

    @Operation(summary = "[Industry] Cancel my order - only allowed before it ships")
    @PreAuthorize("hasRole('INDUSTRY')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order cancelled", response));
    }

    @Operation(summary = "[Industry] View my own orders, optionally filtered by status")
    @PreAuthorize("hasRole('INDUSTRY')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        Page<OrderResponse> response = orderService.getIndustryOrders(user.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders fetched", response));
    }

    @Operation(summary = "[Recycler] View orders placed against my materials, optionally filtered by status")
    @PreAuthorize("hasRole('RECYCLER')")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getReceivedOrders(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        Page<OrderResponse> response = orderService.getRecyclerOrders(user.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders fetched", response));
    }

    @Operation(summary = "[Recycler] Move a paid order forward: PROCESSING -> SHIPPED -> DELIVERED")
    @PreAuthorize("hasRole('RECYCLER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order status updated", response));
    }

    @Operation(summary = "View full details of a single order")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getDetail(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        OrderResponse response = orderService.getOrderDetail(id);

        boolean isBuyer = "INDUSTRY".equals(user.getRole()) && response.getIndustryId().equals(user.getUserId());
        boolean isSeller = "RECYCLER".equals(user.getRole()) && response.getRecyclerId().equals(user.getUserId());
        boolean isAdmin = "ADMIN".equals(user.getRole());

        if (!isBuyer && !isSeller && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "You do not have access to this order", null));
        }

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order fetched", response));
    }

    @Operation(summary = "[Admin] View every order in the system, optionally filtered by status")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status, Pageable pageable) {
        Page<OrderResponse> response = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders fetched", response));
    }
}
