package io.github.techrbaitha.eventledger.account.service;

import io.github.techrbaitha.eventledger.account.dto.EventRequest;
import io.github.techrbaitha.eventledger.account.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.account.entity.AccountTransaction;
import io.github.techrbaitha.eventledger.account.repository.AccountTransactionRepository;
import io.github.techrbaitha.eventledger.account.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.account.dto.AccountDetailsResponse;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class AccountService {

    private final AccountTransactionRepository repository;

    public AccountService(AccountTransactionRepository repository) {
        this.repository = repository;
    }

    public AccountBalanceResponse getBalance(String accountId) {

        List<AccountTransaction> transactions =
                repository.findByAccountIdOrderByEventTimestampAsc(accountId);

        BigDecimal balance = transactions.stream()
                .map(t -> "CREDIT".equalsIgnoreCase(t.getType())
                        ? t.getAmount()
                        : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountBalanceResponse(accountId, balance);
    }

    public AccountDetailsResponse getAccountDetails(String accountId) {

        List<AccountTransaction> transactions =
                repository.findByAccountIdOrderByEventTimestampAsc(accountId);

        List<EventRequest> eventRequests = transactions.stream()
                .map(t -> new EventRequest(
                        t.getEventId(),
                        t.getAccountId(),
                        t.getType(),
                        t.getAmount(),
                        t.getCurrency(),
                        t.getEventTimestamp()))
                .toList();

        return new AccountDetailsResponse(
                accountId,
                getBalance(accountId),
                eventRequests
        );
    }


    public TransactionResponse applyTransaction(EventRequest request) {
        repository.findByEventId(request.eventId())
                .ifPresent(transaction -> {
                    throw new IllegalArgumentException(
                            "Duplicate event: " + request.eventId());
                });

        repository.save(new AccountTransaction(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.eventTimestamp()
        ));

        log.info("Transaction stored. eventId={}", request.eventId());

        return new TransactionResponse(
                "SUCCESS",
                "Transaction applied successfully."
        );
    }

}