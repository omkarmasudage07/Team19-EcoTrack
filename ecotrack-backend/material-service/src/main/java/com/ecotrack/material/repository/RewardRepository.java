package com.ecotrack.material.repository;

import com.ecotrack.material.entity.Reward;
import com.ecotrack.material.enums.RewardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    @Query("SELECT r FROM Reward r WHERE " +
           "(:active IS NULL OR r.active = :active) AND " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:inStockOnly = false OR r.stockQuantity > 0) AND " +
           "(:search IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Reward> searchRewards(
            @Param("active") Boolean active,
            @Param("category") RewardCategory category,
            @Param("inStockOnly") boolean inStockOnly,
            @Param("search") String search,
            Pageable pageable);

    List<Reward> findByActiveTrue();
    long countByActiveTrue();
}
