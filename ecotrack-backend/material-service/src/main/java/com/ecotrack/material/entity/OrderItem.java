package com.ecotrack.material.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * One line item within an Order - a snapshot of the material, quantity
 * and price AT THE TIME OF PURCHASE. We deliberately copy `materialName`
 * and `price` here rather than only referencing the Material row, so the
 * order history stays accurate even if the Recycler later edits or
 * removes that material listing.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Column(name = "material_name", nullable = false, length = 150)
    private String materialName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
