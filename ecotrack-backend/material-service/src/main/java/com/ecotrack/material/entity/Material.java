package com.ecotrack.material.entity;

import com.ecotrack.material.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A recovered raw material a Recycler is offering on the B2B marketplace.
 *
 * There is no separate "Inventory" table - `quantity` and
 * `availabilityStatus` right here on the Material row ARE the inventory.
 * A second table just duplicating the same two columns would only add a
 * join with no extra guarantee, so we dropped it (same simplification
 * pattern as the Auth Service's `roles` table).
 */
@Entity
@Table(name = "materials", indexes = {
        @Index(name = "idx_materials_recycler_id", columnList = "recycler_id"),
        @Index(name = "idx_materials_category_id", columnList = "category_id"),
        @Index(name = "idx_materials_material_name", columnList = "material_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recycler_id", nullable = false)
    private Long recyclerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MaterialCategory category;

    @Column(name = "material_name", nullable = false, length = 150)
    private String materialName;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String purity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 20)
    private String unit; // kg, tonnes, units, etc.

    @Column(name = "price_per_unit", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "warehouse_location", length = 255)
    private String warehouseLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
