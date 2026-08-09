package com.ecotrack.material.service;

import com.ecotrack.material.dto.response.EcoPointsWalletResponse;
import com.ecotrack.material.entity.EcoPointTransaction;
import com.ecotrack.material.entity.EcoPointsWallet;
import com.ecotrack.material.enums.EcoPointTransactionType;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.repository.EcoPointTransactionRepository;
import com.ecotrack.material.repository.EcoPointsWalletRepository;
import com.ecotrack.material.service.impl.EcoPointsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EcoPointsServiceImplTest {

    @Mock
    private EcoPointsWalletRepository walletRepository;

    @Mock
    private EcoPointTransactionRepository transactionRepository;

    @InjectMocks
    private EcoPointsServiceImpl ecoPointsService;

    private EcoPointsWallet testWallet;

    @BeforeEach
    void setUp() {
        testWallet = EcoPointsWallet.builder()
                .id(1L)
                .citizenId(100L)
                .currentBalance(300)
                .totalEarned(500)
                .totalRedeemed(200)
                .build();
    }

    @Test
    @DisplayName("Should return wallet details for existing citizen")
    void getWallet_success() {
        when(walletRepository.findByCitizenId(100L)).thenReturn(Optional.of(testWallet));

        EcoPointsWalletResponse response = ecoPointsService.getWallet(100L);

        assertNotNull(response);
        assertEquals(100L, response.getCitizenId());
        assertEquals(300, response.getCurrentBalance());
        assertEquals(500, response.getTotalEarned());
        assertEquals(200, response.getTotalRedeemed());
        verify(walletRepository, times(1)).findByCitizenId(100L);
    }

    @Test
    @DisplayName("Should award points and update total earned")
    void awardPoints_success() {
        when(walletRepository.findByCitizenId(100L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(EcoPointsWallet.class))).thenAnswer(i -> i.getArgument(0));

        ecoPointsService.awardPoints(100L, 100, "Earned points for pickup");

        assertEquals(400, testWallet.getCurrentBalance());
        assertEquals(600, testWallet.getTotalEarned());
        verify(walletRepository, times(1)).save(testWallet);
        verify(transactionRepository, times(1)).save(any(EcoPointTransaction.class));
    }

    @Test
    @DisplayName("Should deduct points and update total redeemed")
    void deductPoints_success() {
        when(walletRepository.findByCitizenId(100L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(EcoPointsWallet.class))).thenAnswer(i -> i.getArgument(0));

        ecoPointsService.deductPoints(100L, 150, "Redeemed reward");

        assertEquals(150, testWallet.getCurrentBalance());
        assertEquals(350, testWallet.getTotalRedeemed());
        verify(walletRepository, times(1)).save(testWallet);
        verify(transactionRepository, times(1)).save(any(EcoPointTransaction.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when deducting points with insufficient balance")
    void deductPoints_insufficientBalance_throwsException() {
        when(walletRepository.findByCitizenId(100L)).thenReturn(Optional.of(testWallet));

        assertThrows(BusinessException.class, () -> ecoPointsService.deductPoints(100L, 500, "Expensive reward"));
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}
