package com.ecotrack.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Citizen profile. Created automatically right after a Citizen registers
 * through the Auth Service (which calls this service's internal endpoint
 * with the new user's id, name and phone).
 *
 * `userId` is a reference to the `users.id` row in the Auth Service's own
 * database (auth_db). We never join across databases - if we ever need
 * the user's email or account status we ask the Auth Service for it.
 */
@Entity
@Table(name = "citizens", indexes = {
        @Index(name = "idx_citizens_user_id", columnList = "user_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "profile_photo", length = 255)
    private String profilePhoto;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(name = "region_name", length = 100)
    @Builder.Default
    private String regionName = "Pune Region";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
