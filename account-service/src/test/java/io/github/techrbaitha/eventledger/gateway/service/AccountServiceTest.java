package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.gateway.entity.AccountTransaction;
import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import io.github.techrbaitha.eventledger.gateway.exception.DuplicateTransactionException;
import io.github.techrbaitha.eventledger.gateway.repository.AccountTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountTransactionRepository repository;

    @InjectMocks
    private AccountService accountService;

    private EventRequest request;

    @BeforeEach
    void setUp() {

        request = new EventRequest(
                "evt-001",
                "acct-001",
                "CREDIT",
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-05-15T14:02:11Z")
        );
    }

    @Test
    void shouldApplyTransactionSuccessfully() {

        when(repository.findByEventId("evt-001"))
                .thenReturn(Optional.empty());

        when(repository.save(any(AccountTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response =
                accountService.applyTransaction(request);

        assertEquals("SUCCESS", response.status());
        assertEquals("Transaction applied successfully.", response.message());

        verify(repository).save(any(AccountTransaction.class));
    }

    @Test
    void shouldRejectDuplicateTransaction() {

        when(repository.findByEventId("evt-001"))
                .thenReturn(Optional.of(mock(AccountTransaction.class)));

        assertThrows(
                DuplicateTransactionException.class,
                () -> accountService.applyTransaction(request)
        );

        verify(repository, never())
                .save(any(AccountTransaction.class));
    }

    @Test
    void shouldCalculateBalanceCorrectly() {

        List<AccountTransaction> transactions = List.of(
                new AccountTransaction(
                        "evt-1",
                        "acct-001",
                        "CREDIT",
                        new BigDecimal("500"),
                        "USD",
                        Instant.now()
                ),
                new AccountTransaction(
                        "evt-2",
                        "acct-001",
                        TransactionType.DEBIT,
                        new BigDecimal("200"),
                        "USD",
                        Instant.now()
                )
        );

        when(repository.findByAccountIdOrderByEventTimestampAsc("acct-001"))
                .thenReturn(transactions);

        AccountBalanceResponse balance =
                accountService.getBalance("acct-001");

        assertEquals(
                new BigDecimal("300"),
                balance.balance()
        );
    }

    @Test
    void shouldReturnZeroBalanceWhenNoTransactionsExist() {

        when(repository.findByAccountIdOrderByEventTimestampAsc("acct-001"))
                .thenReturn(List.of());

        AccountBalanceResponse balance =
                accountService.getBalance("acct-001");

        assertEquals(
                BigDecimal.ZERO,
                balance.balance()
        );
    }

    @Test
    void shouldReturnTransactionsOrderedByTimestamp() {

        when(repository.findByAccountIdOrderByEventTimestampAsc("acct-001"))
                .thenReturn(List.of());

        accountService.getAccountDetails("acct-001");

        verify(repository)
                .findByAccountIdOrderByEventTimestampAsc("acct-001");
    }
}