package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.PlaceOrderRequest;
import com.ecotrack.material.dto.request.UpdateOrderStatusRequest;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long industryId, PlaceOrderRequest request);

    OrderResponse getOrderDetail(Long orderId);

    Page<OrderResponse> getIndustryOrders(Long industryId, OrderStatus status, Pageable pageable);

    Page<OrderResponse> getRecyclerOrders(Long recyclerId, OrderStatus status, Pageable pageable);

    Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable);

    /** [Recycler] Moves a paid order forward: PROCESSING -> SHIPPED -> DELIVERED. */
    OrderResponse updateOrderStatus(Long orderId, Long recyclerId, UpdateOrderStatusRequest request);

    /** [Industry] Only allowed before the order ships. */
    OrderResponse cancelOrder(Long orderId, Long industryId);
}
