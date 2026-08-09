package com.ecotrack.material.entity;

import com.ecotrack.material.enums.RewardOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward_orders", indexes = {
        @Index(name = "idx_reward_orders_citizen_id", columnList = "citizen_id"),
        @Index(name = "idx_reward_orders_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "citizen_id", nullable = false)
    private Long citizenId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "reward_title", nullable = false, length = 150)
    private String rewardTitle;

    @Column(name = "points_spent", nullable = false)
    private Integer pointsSpent;

    @Column(name = "delivery_address", length = 300)
    private String deliveryAddress;

    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RewardOrderStatus status = RewardOrderStatus.CONFIRMED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
