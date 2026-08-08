package com.ecotrack.pickup.repository;

import com.ecotrack.pickup.entity.WasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WasteCategoryRepository extends JpaRepository<WasteCategory, Long> {
    List<WasteCategory> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
