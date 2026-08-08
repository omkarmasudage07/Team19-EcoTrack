package com.ecotrack.user.repository;

import com.ecotrack.user.entity.Recycler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecyclerRepository extends JpaRepository<Recycler, Long> {
    Optional<Recycler> findByUserId(Long userId);
    boolean existsByCompanyRegistrationNumberIgnoreCase(String companyRegistrationNumber);
    Page<Recycler> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
}
