package com.ecotrack.material.entity;

import com.ecotrack.material.enums.PaymentMethod;
import com.ecotrack.material.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Version 1 shipped with Mock Payment only - no real payment gateway.
 * Razorpay was added as a second option; both share this same table.
 * The three razorpay* fields are null for every Mock payment and only
 * populated when paymentMethod = RAZORPAY.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "transaction_number", nullable = false, unique = true, length = 40)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;

    /** Razorpay's own order id (created before the checkout widget opens). Null for Mock payments. */
    @Column(name = "razorpay_order_id", length = 60)
    private String razorpayOrderId;

    /** Razorpay's payment id, returned by the checkout widget after the user pays. Null until paid, null for Mock. */
    @Column(name = "razorpay_payment_id", length = 60)
    private String razorpayPaymentId;

    /** HMAC signature Razorpay sends back - verified server-side before we trust the payment. Null for Mock. */
    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @CreationTimestamp
    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;

    @Column(length = 255)
    private String remarks;
}
