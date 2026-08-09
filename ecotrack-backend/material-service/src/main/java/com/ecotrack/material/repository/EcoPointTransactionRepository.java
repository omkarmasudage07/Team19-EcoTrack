package com.ecotrack.material.repository;

import com.ecotrack.material.entity.EcoPointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcoPointTransactionRepository extends JpaRepository<EcoPointTransaction, Long> {
    Page<EcoPointTransaction> findByWalletIdOrderByTransactionDateDesc(Long walletId, Pageable pageable);
}
