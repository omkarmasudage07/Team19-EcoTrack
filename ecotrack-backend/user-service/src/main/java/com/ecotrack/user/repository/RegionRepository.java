package com.ecotrack.user.repository;

import com.ecotrack.user.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByNameIgnoreCase(String name);
    List<Region> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
