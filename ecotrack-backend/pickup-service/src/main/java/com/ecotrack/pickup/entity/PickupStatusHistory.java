package com.ecotrack.pickup.entity;

import com.ecotrack.pickup.enums.PickupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * An audit trail of every status change a Pickup goes through, so both
 * the Citizen and the Admin can see a full timeline
 * (Pending -> Accepted -> On The Way -> ... -> Completed).
 */
@Entity
@Table(name = "pickup_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_id", nullable = false)
    private Pickup pickup;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private PickupStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private PickupStatus newStatus;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "updated_at", updatable = false)
    private LocalDateTime updatedAt;
}
