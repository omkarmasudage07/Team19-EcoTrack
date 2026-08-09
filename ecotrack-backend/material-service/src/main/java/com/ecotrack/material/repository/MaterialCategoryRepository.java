package com.ecotrack.material.repository;

import com.ecotrack.material.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long> {
    List<MaterialCategory> findByActiveTrue();
    Optional<MaterialCategory> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
