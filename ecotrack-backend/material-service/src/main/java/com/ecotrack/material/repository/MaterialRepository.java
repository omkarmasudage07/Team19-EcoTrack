package com.ecotrack.material.repository;

import com.ecotrack.material.entity.Material;
import com.ecotrack.material.enums.AvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Page<Material> findByRecyclerId(Long recyclerId, Pageable pageable);

    Page<Material> findByAvailabilityStatus(AvailabilityStatus status, Pageable pageable);

    Page<Material> findByMaterialNameContainingIgnoreCaseAndAvailabilityStatus(
            String materialName, AvailabilityStatus status, Pageable pageable);

    Page<Material> findByCategoryIdAndAvailabilityStatus(
            Long categoryId, AvailabilityStatus status, Pageable pageable);
}
