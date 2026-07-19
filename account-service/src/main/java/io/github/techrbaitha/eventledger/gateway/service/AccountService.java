package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.gateway.dto.AccountDetailsResponse;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.gateway.entity.AccountTransaction;
import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import io.github.techrbaitha.eventledger.gateway.exception.DuplicateTransactionException;
import io.github.techrbaitha.eventledger.gateway.repository.AccountTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
                .map(t -> "CREDIT".equalsIgnoreCase(String.valueOf(t.getType()))
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
                        t.getEventTimestamp()
                ))
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
                    throw new DuplicateTransactionException(request.eventId());
                });

        repository.save(new AccountTransaction(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.eventTimestamp()
        ));

        log.info(
                "traceId={} Transaction stored. eventId={}",
                MDC.get("traceId"),
                request.eventId()
        );

        return new TransactionResponse(
                "SUCCESS",
                "Transaction applied successfully."
        );
    }
}