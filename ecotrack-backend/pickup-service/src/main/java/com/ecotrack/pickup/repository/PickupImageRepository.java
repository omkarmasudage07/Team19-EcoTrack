package com.ecotrack.pickup.repository;

import com.ecotrack.pickup.entity.PickupImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickupImageRepository extends JpaRepository<PickupImage, Long> {
    List<PickupImage> findByPickupId(Long pickupId);
}
