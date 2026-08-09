package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.response.EcoPointTransactionResponse;
import com.ecotrack.material.dto.response.EcoPointsWalletResponse;
import com.ecotrack.material.entity.EcoPointTransaction;
import com.ecotrack.material.entity.EcoPointsWallet;
import com.ecotrack.material.enums.EcoPointTransactionType;
import com.ecotrack.material.mapper.MaterialMapper;
import com.ecotrack.material.repository.EcoPointTransactionRepository;
import com.ecotrack.material.repository.EcoPointsWalletRepository;
import com.ecotrack.material.service.EcoPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcoPointsServiceImpl implements EcoPointsService {

    private final EcoPointsWalletRepository walletRepository;
    private final EcoPointTransactionRepository transactionRepository;

    @Override
    public EcoPointsWalletResponse getWallet(Long citizenId) {
        EcoPointsWallet wallet = walletRepository.findByCitizenId(citizenId)
                .orElseGet(() -> EcoPointsWallet.builder().citizenId(citizenId).currentBalance(0).build());
        return MaterialMapper.toResponse(wallet);
    }

    @Override
    public Page<EcoPointTransactionResponse> getTransactions(Long citizenId, Pageable pageable) {
        EcoPointsWallet wallet = walletRepository.findByCitizenId(citizenId).orElse(null);
        if (wallet == null) {
            return Page.empty(pageable);
        }
        return transactionRepository.findByWalletIdOrderByTransactionDateDesc(wallet.getId(), pageable)
                .map(MaterialMapper::toResponse);
    }

    @Override
    @Transactional
    public void awardPoints(Long citizenId, int points, String description) {
        EcoPointsWallet wallet = walletRepository.findByCitizenId(citizenId)
                .orElseGet(() -> walletRepository.save(
                        EcoPointsWallet.builder().citizenId(citizenId).currentBalance(0).totalEarned(0).totalRedeemed(0).build()));

        wallet.setCurrentBalance(wallet.getCurrentBalance() + points);
        wallet.setTotalEarned(wallet.getTotalEarned() + points);
        wallet = walletRepository.save(wallet);

        transactionRepository.save(EcoPointTransaction.builder()
                .wallet(wallet)
                .points(points)
                .transactionType(EcoPointTransactionType.CREDIT)
                .description(description)
                .build());

        log.info("Awarded {} EcoPoints to citizen {} ({}). New balance: {}",
                points, citizenId, description, wallet.getCurrentBalance());
    }

    @Override
    @Transactional
    public void deductPoints(Long citizenId, int points, String description) {
        EcoPointsWallet wallet = walletRepository.findByCitizenId(citizenId)
                .orElseThrow(() -> new com.ecotrack.material.exception.BusinessException(
                        "EcoPoints wallet not found. Earn points before redeeming rewards.", HttpStatus.BAD_REQUEST));

        if (wallet.getCurrentBalance() < points) {
            throw new com.ecotrack.material.exception.BusinessException(
                    "Insufficient EcoPoints balance. Required: " + points + ", Available: " + wallet.getCurrentBalance(),
                    HttpStatus.BAD_REQUEST);
        }

        wallet.setCurrentBalance(wallet.getCurrentBalance() - points);
        wallet.setTotalRedeemed(wallet.getTotalRedeemed() + points);
        wallet = walletRepository.save(wallet);

        transactionRepository.save(EcoPointTransaction.builder()
                .wallet(wallet)
                .points(points)
                .transactionType(EcoPointTransactionType.DEBIT)
                .description(description)
                .build());

        log.info("Deducted {} EcoPoints from citizen {} ({}). Remaining balance: {}",
                points, citizenId, description, wallet.getCurrentBalance());
    }
}
