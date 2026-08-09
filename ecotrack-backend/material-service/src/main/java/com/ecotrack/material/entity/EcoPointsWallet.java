package com.ecotrack.material.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Every Citizen has exactly one EcoPoints wallet, created lazily the
 * first time they earn points (i.e. the first time one of their pickups
 * is completed).
 */
@Entity
@Table(name = "ecopoints_wallets", indexes = {
        @Index(name = "idx_wallets_citizen_id", columnList = "citizen_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoPointsWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citizen_id", nullable = false, unique = true)
    private Long citizenId;

    @Column(name = "current_balance", nullable = false)
    @Builder.Default
    private Integer currentBalance = 0;

    @Column(name = "total_earned", nullable = false)
    @Builder.Default
    private Integer totalEarned = 0;

    @Column(name = "total_redeemed", nullable = false)
    @Builder.Default
    private Integer totalRedeemed = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
