package com.ecotrack.pickup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Images attached to a pickup - either the Citizen's photos of the waste
 * when scheduling, or the Recycler's collection-proof photo. `imageType`
 * tells them apart. Only the file path is stored; the actual file lives
 * on disk (see the docx/pptx-style file upload note in the README).
 */
@Entity
@Table(name = "pickup_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_id", nullable = false)
    private Pickup pickup;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "image_type", nullable = false, length = 30)
    @Builder.Default
    private String imageType = "WASTE_PHOTO"; // WASTE_PHOTO or COLLECTION_PROOF

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
