package com.ecotrack.material.repository;

import com.ecotrack.material.entity.MaterialImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialImageRepository extends JpaRepository<MaterialImage, Long> {
    List<MaterialImage> findByMaterialId(Long materialId);
}
