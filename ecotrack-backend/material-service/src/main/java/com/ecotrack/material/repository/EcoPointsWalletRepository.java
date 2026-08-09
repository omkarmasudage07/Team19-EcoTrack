package com.ecotrack.material.repository;

import com.ecotrack.material.entity.EcoPointsWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EcoPointsWalletRepository extends JpaRepository<EcoPointsWallet, Long> {
    Optional<EcoPointsWallet> findByCitizenId(Long citizenId);
}
