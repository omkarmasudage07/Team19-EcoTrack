package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.MockPaymentRequest;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.dto.response.RazorpayOrderResponse;
import com.ecotrack.material.entity.Order;
import com.ecotrack.material.entity.Payment;
import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.enums.PaymentMethod;
import com.ecotrack.material.enums.PaymentStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.producer.MaterialEventProducer;
import com.ecotrack.material.repository.OrderItemRepository;
import com.ecotrack.material.repository.OrderRepository;
import com.ecotrack.material.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MaterialEventProducer materialEventProducer;

    @Mock
    private RazorpayClient razorpayClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "razorpayKeyId", "rzp_test_placeholder");
        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "dummy_secret");

        sampleOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20260807-1001")
                .industryId(5L)
                .recyclerId(10L)
                .totalAmount(new BigDecimal("2500.00"))
                .orderStatus(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully process instant Mock payment")
    void testProcessMockPaymentSuccess() {
        MockPaymentRequest request = new MockPaymentRequest(PaymentMethod.MOCK_UPI);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Collections.emptyList());

        OrderResponse response = paymentService.processPayment(1L, 5L, request);

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, sampleOrder.getOrderStatus());
        assertEquals(PaymentStatus.SUCCESS, sampleOrder.getPaymentStatus());
        verify(materialEventProducer).publishPaymentSuccessful(any());
    }

    @Test
    @DisplayName("Should create Razorpay order with test sandbox fallback when keys are placeholders")
    void testCreateRazorpayOrderSandboxFallback() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        RazorpayOrderResponse response = paymentService.createRazorpayOrder(1L, 5L);

        assertNotNull(response);
        assertTrue(response.getRazorpayOrderId().startsWith("order_rzp_test_"));
        assertEquals(250000L, response.getAmountInPaise());
    }

    @Test
    @DisplayName("Should throw BusinessException when paying for already paid order")
    void testProcessPaymentAlreadyPaid() {
        sampleOrder.setPaymentStatus(PaymentStatus.SUCCESS);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        MockPaymentRequest request = new MockPaymentRequest(PaymentMethod.MOCK_UPI);
        assertThrows(BusinessException.class, () -> paymentService.processPayment(1L, 5L, request));
    }
}
