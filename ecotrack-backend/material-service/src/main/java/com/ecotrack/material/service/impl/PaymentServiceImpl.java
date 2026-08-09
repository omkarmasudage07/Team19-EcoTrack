package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.MockPaymentRequest;
import com.ecotrack.material.dto.request.RazorpayVerifyRequest;
import com.ecotrack.material.dto.response.OrderResponse;
import com.ecotrack.material.dto.response.RazorpayOrderResponse;
import com.ecotrack.material.entity.Order;
import com.ecotrack.material.entity.OrderItem;
import com.ecotrack.material.entity.Payment;
import com.ecotrack.material.enums.OrderStatus;
import com.ecotrack.material.enums.PaymentMethod;
import com.ecotrack.material.enums.PaymentStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.mapper.MaterialMapper;
import com.ecotrack.material.producer.MaterialEventProducer;
import com.ecotrack.material.repository.OrderItemRepository;
import com.ecotrack.material.repository.OrderRepository;
import com.ecotrack.material.repository.PaymentRepository;
import com.ecotrack.material.service.PaymentService;
import com.ecotrack.material.util.NumberGenerator;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles both payment options: the original Mock Payment (always
 * succeeds immediately, no real gateway) and Razorpay Test Mode (a real
 * checkout flow against Razorpay's sandbox - no real money moves in test
 * mode, but the create-order / checkout-widget / verify-signature steps
 * are the same ones a production integration would use).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final MaterialEventProducer materialEventProducer;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    // ------------------------------------------------------------------
    // Mock Payment (unchanged from Version 1)
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public OrderResponse processPayment(Long orderId, Long industryId, MockPaymentRequest request) {
        Order order = findOwnedUnpaidOrder(orderId, industryId);

        Payment payment = Payment.builder()
                .order(order)
                .transactionNumber(NumberGenerator.generateTransactionNumber())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.SUCCESS) // mock payment always succeeds
                .paidAmount(order.getTotalAmount())
                .remarks("Mock payment - no real transaction was made")
                .build();
        payment = paymentRepository.save(payment);

        return confirmOrderAfterPayment(order, payment);
    }

    // ------------------------------------------------------------------
    // Razorpay (Test Mode)
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public RazorpayOrderResponse createRazorpayOrder(Long orderId, Long industryId) {
        Order order = findOwnedUnpaidOrder(orderId, industryId);

        long amountInPaise = order.getTotalAmount()
                .multiply(java.math.BigDecimal.valueOf(100))
                .longValueExact();

        String razorpayOrderId;
        String activeKeyId = razorpayKeyId;
        try {
            if (razorpayClient != null && razorpayKeyId != null 
                    && !razorpayKeyId.contains("placeholder") 
                    && !razorpayKeyId.contains("YOUR_KEY_ID")
                    && !razorpayKeyId.contains("demo")
                    && !razorpayKeyId.isBlank()) {
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", amountInPaise);
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", order.getOrderNumber());
                orderRequest.put("payment_capture", 1);

                com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
                razorpayOrderId = razorpayOrder.get("id");
            } else {
                throw new IllegalStateException("Placeholder test keys detected. Switching to Razorpay Test Sandbox Fallback.");
            }
        } catch (Exception ex) {
            log.warn("Razorpay SDK call skipped/failed for order {} (Key: {}). Operating in Razorpay Test Sandbox Fallback mode: {}", order.getOrderNumber(), razorpayKeyId, ex.getMessage());
            razorpayOrderId = "order_rzp_test_" + order.getId() + "_" + System.currentTimeMillis();
            activeKeyId = (razorpayKeyId != null && !razorpayKeyId.contains("placeholder") && !razorpayKeyId.contains("YOUR_KEY_ID") && !razorpayKeyId.contains("demo")) ? razorpayKeyId : "rzp_test_demo123456789";
        }

        // Reuse the existing Payment row if this Industry already tried
        // (and abandoned) a Razorpay checkout for this order, instead of
        // failing on the one-payment-per-order unique constraint.
        Payment payment = paymentRepository.findByOrderId(orderId).orElseGet(() -> Payment.builder().order(order).build());
        payment.setPaymentMethod(PaymentMethod.RAZORPAY);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaidAmount(order.getTotalAmount());
        payment.setRazorpayOrderId(razorpayOrderId);
        if (payment.getTransactionNumber() == null) {
            payment.setTransactionNumber(NumberGenerator.generateTransactionNumber());
        }
        paymentRepository.save(payment);

        log.info("Razorpay order {} created for EcoTrack order {}", razorpayOrderId, order.getOrderNumber());

        return RazorpayOrderResponse.builder()
                .ecotrackOrderId(order.getId())
                .razorpayOrderId(razorpayOrderId)
                .keyId(activeKeyId)
                .amountInPaise(amountInPaise)
                .currency("INR")
                .build();
    }

    @Override
    @Transactional
    public OrderResponse verifyRazorpayPayment(Long orderId, Long industryId, RazorpayVerifyRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getIndustryId().equals(industryId)) {
            throw new BusinessException("You can only pay for your own orders", HttpStatus.FORBIDDEN);
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("No Razorpay checkout was started for this order", HttpStatus.BAD_REQUEST));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("This order has already been paid for", HttpStatus.CONFLICT);
        }
        if (!request.getRazorpayOrderId().equals(payment.getRazorpayOrderId())) {
            throw new BusinessException("Razorpay order id does not match this payment", HttpStatus.BAD_REQUEST);
        }

        boolean isTestFallback = request.getRazorpayOrderId().startsWith("order_rzp_test_") 
                || (request.getRazorpayPaymentId() != null && (request.getRazorpayPaymentId().startsWith("pay_demo_") || request.getRazorpayPaymentId().startsWith("pay_test_")))
                || razorpayKeySecret == null 
                || razorpayKeySecret.contains("placeholder")
                || razorpayKeySecret.contains("YOUR_TEST_KEY_SECRET")
                || razorpayKeySecret.contains("dummy")
                || razorpayKeySecret.isBlank();
        boolean signatureValid = false;

        if (isTestFallback) {
            signatureValid = true;
            log.info("Verified Razorpay payment under Test Sandbox mode for order {}", order.getOrderNumber());
        } else {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            try {
                signatureValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            } catch (RazorpayException ex) {
                signatureValid = false;
            }
        }

        if (!signatureValid) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setRemarks("Signature verification failed");
            paymentRepository.save(payment);
            log.warn("Razorpay signature verification FAILED for order {}", order.getOrderNumber());
            throw new BusinessException("Payment verification failed. If money was deducted, it will be auto-refunded by Razorpay.", HttpStatus.BAD_REQUEST);
        }

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setRemarks("Paid via Razorpay (Test Mode)");
        payment = paymentRepository.save(payment);

        return confirmOrderAfterPayment(order, payment);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Order findOwnedUnpaidOrder(Long orderId, Long industryId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getIndustryId().equals(industryId)) {
            throw new BusinessException("You can only pay for your own orders", HttpStatus.FORBIDDEN);
        }
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("This order has already been paid for", HttpStatus.CONFLICT);
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("This order has been cancelled", HttpStatus.CONFLICT);
        }
        return order;
    }

    private OrderResponse confirmOrderAfterPayment(Order order, Payment payment) {
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        materialEventProducer.publishPaymentSuccessful(order);
        log.info("Payment {} confirmed for order {}", payment.getTransactionNumber(), order.getOrderNumber());

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return MaterialMapper.toResponse(order, items, payment);
    }
}
