package com.ecotrack.user.repository;

import com.ecotrack.user.entity.IndustryApplication;
import com.ecotrack.user.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryApplicationRepository extends JpaRepository<IndustryApplication, Long> {
    boolean existsByEmailAndStatus(String email, ApprovalStatus status);
    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
    Page<IndustryApplication> findByStatus(ApprovalStatus status, Pageable pageable);
}
