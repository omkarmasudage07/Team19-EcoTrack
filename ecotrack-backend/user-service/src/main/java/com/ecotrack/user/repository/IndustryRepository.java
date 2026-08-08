package com.ecotrack.user.repository;

import com.ecotrack.user.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry, Long> {
    Optional<Industry> findByUserId(Long userId);
    boolean existsByCompanyRegistrationNumberIgnoreCase(String companyRegistrationNumber);
    Page<Industry> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
}
