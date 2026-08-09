package com.ecotrack.material.repository;

import com.ecotrack.material.entity.EcoPointRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EcoPointRuleRepository extends JpaRepository<EcoPointRule, Long> {
    Optional<EcoPointRule> findByCategoryNameIgnoreCase(String categoryName);
}
