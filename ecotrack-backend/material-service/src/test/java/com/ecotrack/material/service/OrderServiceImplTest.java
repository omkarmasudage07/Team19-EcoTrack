package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.PlaceOrderRequest;
import com.ecotrack.material.dto.request.UpdateOrderStatusRequest;
import com.ecotrack.material.entity.Material;
import com.ecotrack.material.entity.MaterialCategory;
import com.ecotrack.material.entity.Order;
import com.ecotrack.material.enums.AvailabilityStatus;
import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.producer.MaterialEventProducer;
import com.ecotrack.material.repository.MaterialRepository;
import com.ecotrack.material.repository.OrderItemRepository;
import com.ecotrack.material.repository.OrderRepository;
import com.ecotrack.material.repository.PaymentRepository;
import com.ecotrack.material.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private MaterialEventProducer materialEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Material material(Long id, Long recyclerId, BigDecimal quantity, BigDecimal price) {
        MaterialCategory category = MaterialCategory.builder().id(1L).name("Copper").active(true).build();
        return Material.builder()
                .id(id)
                .recyclerId(recyclerId)
                .category(category)
                .materialName("Copper Wire")
                .quantity(quantity)
                .unit("kg")
                .pricePerUnit(price)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .build();
    }

    @Test
    void placeOrder_rejectsItemsFromDifferentRecyclers() {
        Material materialA = material(1L, 100L, BigDecimal.TEN, BigDecimal.valueOf(50));
        Material materialB = material(2L, 200L, BigDecimal.TEN, BigDecimal.valueOf(50));

        when(materialRepository.findById(1L)).thenReturn(Optional.of(materialA));
        when(materialRepository.findById(2L)).thenReturn(Optional.of(materialB));

        PlaceOrderRequest request = new PlaceOrderRequest(List.of(
                new PlaceOrderRequest.OrderItemRequest(1L, BigDecimal.ONE),
                new PlaceOrderRequest.OrderItemRequest(2L, BigDecimal.ONE)
        ));

        assertThatThrownBy(() -> orderService.placeOrder(500L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("same Recycler");
    }

    @Test
    void placeOrder_rejectsWhenNotEnoughStock() {
        Material material = material(1L, 100L, BigDecimal.valueOf(5), BigDecimal.valueOf(50));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        PlaceOrderRequest request = new PlaceOrderRequest(List.of(
                new PlaceOrderRequest.OrderItemRequest(1L, BigDecimal.valueOf(10))
        ));

        assertThatThrownBy(() -> orderService.placeOrder(500L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is available");
    }

    @Test
    void updateOrderStatus_rejectsSkippingAStage() {
        Order order = Order.builder()
                .id(10L).orderNumber("ORD-1").industryId(1L).recyclerId(100L)
                .totalAmount(BigDecimal.TEN).orderStatus(OrderStatus.CONFIRMED)
                .build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.DELIVERED);

        assertThatThrownBy(() -> orderService.updateOrderStatus(10L, 100L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void updateOrderStatus_allowsCorrectNextStage() {
        Order order = Order.builder()
                .id(10L).orderNumber("ORD-1").industryId(1L).recyclerId(100L)
                .totalAmount(BigDecimal.TEN).orderStatus(OrderStatus.CONFIRMED)
                .build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findByOrderId(10L)).thenReturn(Collections.emptyList());
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.empty());

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.PROCESSING);

        var result = orderService.updateOrderStatus(10L, 100L, request);

        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void cancelOrder_rejectsOnceShipped() {
        Order order = Order.builder()
                .id(10L).orderNumber("ORD-1").industryId(1L).recyclerId(100L)
                .totalAmount(BigDecimal.TEN).orderStatus(OrderStatus.SHIPPED)
                .build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already shipped");
    }
}
