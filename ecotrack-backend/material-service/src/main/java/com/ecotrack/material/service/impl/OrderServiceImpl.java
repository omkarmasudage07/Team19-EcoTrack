package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.PlaceOrderRequest;
import com.ecotrack.material.dto.request.UpdateOrderStatusRequest;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.entity.Material;
import com.ecotrack.material.entity.Order;
import com.ecotrack.material.entity.OrderItem;
import com.ecotrack.material.entity.Payment;
import com.ecotrack.material.enums.AvailabilityStatus;
import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.mapper.MaterialMapper;
import com.ecotrack.material.producer.MaterialEventProducer;
import com.ecotrack.material.repository.MaterialRepository;
import com.ecotrack.material.repository.OrderItemRepository;
import com.ecotrack.material.repository.OrderRepository;
import com.ecotrack.material.repository.PaymentRepository;
import com.ecotrack.material.service.OrderService;
import com.ecotrack.material.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MaterialRepository materialRepository;
    private final PaymentRepository paymentRepository;
    private final MaterialEventProducer materialEventProducer;

    /** A paid order can only move forward one step at a time, same pattern as Pickup Service. */
    private static final Map<OrderStatus, OrderStatus> NEXT_STATUS = Map.of(
            OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
            OrderStatus.PROCESSING, OrderStatus.SHIPPED,
            OrderStatus.SHIPPED, OrderStatus.DELIVERED
    );

    @Override
    @Transactional
    public OrderResponse placeOrder(Long industryId, PlaceOrderRequest request) {
        Long recyclerId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
                .orderNumber(NumberGenerator.generateOrderNumber())
                .industryId(industryId)
                .totalAmount(BigDecimal.ZERO) // set correctly below, after validating every item
                .paymentStatus(com.ecotrack.material.enums.PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PLACED)
                .build();

        for (PlaceOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Material material = materialRepository.findById(itemRequest.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Material not found with id: " + itemRequest.getMaterialId()));

            if (recyclerId == null) {
                recyclerId = material.getRecyclerId();
            } else if (!recyclerId.equals(material.getRecyclerId())) {
                throw new BusinessException(
                        "All items in one order must come from the same Recycler. Please place separate orders.",
                        HttpStatus.BAD_REQUEST);
            }

            if (material.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
                throw new BusinessException(material.getMaterialName() + " is currently out of stock", HttpStatus.CONFLICT);
            }
            if (material.getQuantity().compareTo(itemRequest.getQuantity()) < 0) {
                throw new BusinessException(
                        "Only " + material.getQuantity() + " " + material.getUnit() + " of " +
                                material.getMaterialName() + " is available", HttpStatus.CONFLICT);
            }

            BigDecimal subtotal = material.getPricePerUnit().multiply(itemRequest.getQuantity());
            totalAmount = totalAmount.add(subtotal);

            // Reserve the stock immediately on order placement.
            material.setQuantity(material.getQuantity().subtract(itemRequest.getQuantity()));
            if (material.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                material.setAvailabilityStatus(AvailabilityStatus.OUT_OF_STOCK);
            }
            materialRepository.save(material);

            order.setRecyclerId(recyclerId);
            order.setTotalAmount(totalAmount);
        }

        order = orderRepository.save(order);

        for (PlaceOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Material material = materialRepository.findById(itemRequest.getMaterialId()).orElseThrow();
            orderItemRepository.save(OrderItem.builder()
                    .order(order)
                    .materialId(material.getId())
                    .materialName(material.getMaterialName())
                    .quantity(itemRequest.getQuantity())
                    .price(material.getPricePerUnit())
                    .subtotal(material.getPricePerUnit().multiply(itemRequest.getQuantity()))
                    .build());
        }

        materialEventProducer.publishOrderPlaced(order);
        log.info("Order {} placed by industry {} for recycler {}", order.getOrderNumber(), industryId, recyclerId);

        return getOrderDetail(order.getId());
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
        return MaterialMapper.toResponse(order, items, payment);
    }

    @Override
    public Page<OrderResponse> getIndustryOrders(Long industryId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status != null)
                ? orderRepository.findByIndustryIdAndOrderStatus(industryId, status, pageable)
                : orderRepository.findByIndustryId(industryId, pageable);
        return orders.map(order -> MaterialMapper.toResponse(order, List.of(), null));
    }

    @Override
    public Page<OrderResponse> getRecyclerOrders(Long recyclerId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status != null)
                ? orderRepository.findByRecyclerIdAndOrderStatus(recyclerId, status, pageable)
                : orderRepository.findByRecyclerId(recyclerId, pageable);
        return orders.map(order -> MaterialMapper.toResponse(order, List.of(), null));
    }

    @Override
    public Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status != null)
                ? orderRepository.findByOrderStatus(status, pageable)
                : orderRepository.findAll(pageable);
        return orders.map(order -> MaterialMapper.toResponse(order, List.of(), null));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Long recyclerId, UpdateOrderStatusRequest request) {
        Order order = findOrderOrThrow(orderId);

        if (!recyclerId.equals(order.getRecyclerId())) {
            throw new BusinessException("This order does not belong to you", HttpStatus.FORBIDDEN);
        }

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus expectedNext = NEXT_STATUS.get(currentStatus);

        if (expectedNext == null || expectedNext != request.getStatus()) {
            throw new BusinessException(
                    "Invalid status transition: an order that is " + currentStatus +
                            " can only move to " + (expectedNext != null ? expectedNext : "no further status"),
                    HttpStatus.BAD_REQUEST);
        }

        order.setOrderStatus(request.getStatus());
        order = orderRepository.save(order);

        if (request.getStatus() == OrderStatus.SHIPPED) {
            materialEventProducer.publishOrderShipped(order);
        } else if (request.getStatus() == OrderStatus.DELIVERED) {
            materialEventProducer.publishOrderDelivered(order);
        }

        log.info("Order {} status changed {} -> {}", order.getOrderNumber(), currentStatus, request.getStatus());
        return getOrderDetail(order.getId());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long industryId) {
        Order order = findOrderOrThrow(orderId);

        if (!order.getIndustryId().equals(industryId)) {
            throw new BusinessException("You can only cancel your own orders", HttpStatus.FORBIDDEN);
        }
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("This order has already shipped and can no longer be cancelled", HttpStatus.CONFLICT);
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("This order is already cancelled", HttpStatus.CONFLICT);
        }

        // Release the reserved stock back to each Material.
        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            materialRepository.findById(item.getMaterialId()).ifPresent(material -> {
                material.setQuantity(material.getQuantity().add(item.getQuantity()));
                material.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
                materialRepository.save(material);
            });
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        log.info("Order {} cancelled by industry {}", order.getOrderNumber(), industryId);
        return getOrderDetail(order.getId());
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
}
