package com.ecotrack.pickup.repository;

import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.enums.PickupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickupRepository extends JpaRepository<Pickup, Long> {

    Optional<Pickup> findByPickupNumber(String pickupNumber);

    Page<Pickup> findByCitizenId(Long citizenId, Pageable pageable);

    Page<Pickup> findByCitizenIdAndStatus(Long citizenId, PickupStatus status, Pageable pageable);

    Page<Pickup> findByRecyclerId(Long recyclerId, Pageable pageable);

    Page<Pickup> findByRecyclerIdAndStatus(Long recyclerId, PickupStatus status, Pageable pageable);

    Page<Pickup> findByStatus(PickupStatus status, Pageable pageable);

    /** Pickups no Recycler has accepted yet - what a Recycler sees as "available". */
    Page<Pickup> findByStatusAndRecyclerIdIsNull(PickupStatus status, Pageable pageable);

    Page<Pickup> findByStatusAndRecyclerIdIsNullAndRegionNameIgnoreCase(PickupStatus status, String regionName, Pageable pageable);

    long countByStatus(PickupStatus status);

    long countByCitizenIdAndStatus(Long citizenId, PickupStatus status);

    long countByRecyclerIdAndStatus(Long recyclerId, PickupStatus status);
}
