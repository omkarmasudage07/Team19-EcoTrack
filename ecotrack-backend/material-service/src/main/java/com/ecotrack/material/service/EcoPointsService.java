package com.ecotrack.material.service;

import com.ecotrack.material.dto.response.EcoPointTransactionResponse;
import com.ecotrack.material.dto.response.EcoPointsWalletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EcoPointsService {

    EcoPointsWalletResponse getWallet(Long citizenId);

    Page<EcoPointTransactionResponse> getTransactions(Long citizenId, Pageable pageable);

    /** Creates the wallet on first use. Called when a pickup completes. */
    void awardPoints(Long citizenId, int points, String description);

    /** Deducts points from citizen wallet upon reward redemption. */
    void deductPoints(Long citizenId, int points, String description);
}
