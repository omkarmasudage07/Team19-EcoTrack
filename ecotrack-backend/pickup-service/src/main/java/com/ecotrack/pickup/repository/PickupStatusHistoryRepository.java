package com.ecotrack.pickup.repository;

import com.ecotrack.pickup.entity.PickupStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickupStatusHistoryRepository extends JpaRepository<PickupStatusHistory, Long> {
    List<PickupStatusHistory> findByPickupIdOrderByUpdatedAtAsc(Long pickupId);
}
