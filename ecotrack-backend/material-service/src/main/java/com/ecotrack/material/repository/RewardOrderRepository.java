package com.ecotrack.material.repository;

import com.ecotrack.material.entity.RewardOrder;
import com.ecotrack.material.enums.RewardOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardOrderRepository extends JpaRepository<RewardOrder, Long> {

    Page<RewardOrder> findByCitizenId(Long citizenId, Pageable pageable);

    Page<RewardOrder> findByStatus(RewardOrderStatus status, Pageable pageable);

    long countByStatus(RewardOrderStatus status);

    @Query("SELECT SUM(ro.pointsSpent) FROM RewardOrder ro WHERE ro.status != 'CANCELLED'")
    Long sumTotalPointsSpent();

    @Query("SELECT ro.rewardTitle, COUNT(ro) FROM RewardOrder ro GROUP BY ro.rewardTitle ORDER BY COUNT(ro) DESC")
    List<Object[]> findMostRedeemedRewards(Pageable pageable);
}
