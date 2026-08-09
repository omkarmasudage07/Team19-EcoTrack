package com.ecotrack.pickup.entity;

import com.ecotrack.pickup.enums.PickupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single e-waste pickup request from a Citizen.
 *
 * `citizenId` and `recyclerId` are references to userId values owned by
 * Auth Service / User Service - we never join across databases, so if we
 * need the Citizen's name or the Recycler's company name we ask User
 * Service for it (via Feign) when building a response.
 */
@Entity
@Table(name = "pickups", indexes = {
        @Index(name = "idx_pickups_pickup_number", columnList = "pickup_number", unique = true),
        @Index(name = "idx_pickups_citizen_id", columnList = "citizen_id"),
        @Index(name = "idx_pickups_recycler_id", columnList = "recycler_id"),
        @Index(name = "idx_pickups_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pickup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pickup_number", nullable = false, unique = true, length = 30)
    private String pickupNumber;

    @Column(name = "citizen_id", nullable = false)
    private Long citizenId;

    /** Null until a Recycler accepts the pickup. */
    @Column(name = "recycler_id")
    private Long recyclerId;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "pickup_city", length = 50)
    private String pickupCity;

    @Column(name = "pickup_pincode", length = 10)
    private String pickupPincode;

    @Column(name = "region_name", length = 100)
    @Builder.Default
    private String regionName = "Pune Region";

    /**
     * Optional map coordinates the Citizen drops a pin at when scheduling.
     * Nullable - a Citizen can still type a plain address without using
     * the map, so existing pickups (and any Citizen who skips the map)
     * are unaffected.
     */
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "pickup_time_slot", nullable = false, length = 30)
    private String pickupTimeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_category_id", nullable = false)
    private WasteCategory wasteCategory;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PickupStatus status = PickupStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
