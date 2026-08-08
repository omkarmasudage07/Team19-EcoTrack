package com.ecotrack.user.entity;

import com.ecotrack.user.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Submitted by a prospective Recycler Partner BEFORE they have any login
 * credentials. Per the business rule, a Recycler cannot register
 * directly - they must submit this application, wait for Admin review,
 * and only receive credentials after approval.
 */
@Entity
@Table(name = "recycler_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "registration_number", nullable = false, length = 50)
    private String registrationNumber;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "contact_person", nullable = false, length = 100)
    private String contactPerson;

    @Column(nullable = false, length = 15)
    private String phone;

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

    /** Comma-separated relative file paths - the actual files live on disk, only the paths are stored here. */
    @Column(length = 1000)
    private String documents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(name = "submitted_date", updatable = false)
    private LocalDateTime submittedDate;

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(name = "reviewed_by")
    private Long reviewedBy;
}
