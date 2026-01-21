package com.example.playgame.service.impl;

import com.example.playgame.dto.mapper.TopUpDtoMapper;
import com.example.playgame.dto.topUp.TopUpResponseDto;
import com.example.playgame.entity.Account;
import com.example.playgame.entity.TopUp;
import com.example.playgame.exception.InsufficientFundsException;
import com.example.playgame.exception.InvalidAmountException;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.TopUpNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.TopUpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopUpServiceTest {
    @Mock
    private TopUpRepository topUpRepository;

    @Mock
    private TopUpDtoMapper topUpDtoMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TopUpServiceImpl topUpService;

    private TopUp topUp;
    private TopUpResponseDto topUpResponseDto;
    private Account account;

    @BeforeEach
    public void setUp() {
        topUp = new TopUp();
        topUp.setId(1L);
        topUp.setAmount(BigDecimal.valueOf(100));

        topUpResponseDto = new TopUpResponseDto();
        topUpResponseDto.setId(1L);
        topUpResponseDto.setAmount(BigDecimal.valueOf(100));

        account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(200));
    }

    @Test
    public void testGetTopUpById_Success() {
        when(topUpRepository.findById(1L)).thenReturn(Optional.of(topUp));
        when(topUpDtoMapper.topUpToTopUpDto(topUp)).thenReturn(topUpResponseDto);

        TopUpResponseDto result = topUpService.getTopUpById(1L);

        assertEquals(topUpResponseDto, result);
    }

    @Test
    public void testGetTopUpById_NotFound() {
        when(topUpRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TopUpNotFoundException.class, () -> topUpService.getTopUpById(1L));
    }

    @Test
    public void testGetTopUpsByAccountId_Success() {
        when(accountRepository.existsById(1L)).thenReturn(true);
        Page<TopUp> topUps = new PageImpl<>(Collections.singletonList(topUp));
        when(topUpRepository.findByAccountId(1L, PageRequest.of(0, 10))).thenReturn(topUps);
        when(topUpDtoMapper.topUpsToTopUpDtos(topUps.getContent())).thenReturn(Collections.singletonList(topUpResponseDto));

        List<TopUpResponseDto> result = topUpService.getTopUpsByAccountId(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(topUpResponseDto, result.get(0));
    }

    @Test
    public void testGetTopUpsByAccountId_AccountNotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> topUpService.getTopUpsByAccountId(1L, 0, 10));
    }

    @Test
    public void testDeposit_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        topUpService.deposit(1L, BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(300), account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(topUpRepository, times(1)).save(any(TopUp.class));
    }

    @Test
    public void testDeposit_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> topUpService.deposit(1L, BigDecimal.valueOf(100)));
    }

    @Test
    public void testDeposit_InvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> topUpService.deposit(1L, BigDecimal.valueOf(-100)));
    }

    @Test
    public void testTransferFunds_Success() {
        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));

        topUpService.transferFunds(1L, 2L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals(BigDecimal.valueOf(150), receiver.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).save(receiver);
        verify(topUpRepository, times(1)).save(any(TopUp.class));
    }

    @Test
    public void testTransferFunds_SenderNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> topUpService.transferFunds(1L, 2L, BigDecimal.valueOf(50)));
    }

    @Test
    public void testTransferFunds_ReceiverNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> topUpService.transferFunds(1L, 2L, BigDecimal.valueOf(50)));
    }

    @Test
    public void testTransferFunds_InsufficientFunds() {
        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(InsufficientFundsException.class, () -> topUpService.transferFunds(1L, 2L, BigDecimal.valueOf(300)));
    }
}
