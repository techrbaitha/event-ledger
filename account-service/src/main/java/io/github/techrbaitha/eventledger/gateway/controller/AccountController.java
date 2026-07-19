package io.github.techrbaitha.eventledger.gateway.controller;

import io.github.techrbaitha.eventledger.gateway.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.gateway.dto.AccountDetailsResponse;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.gateway.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> applyTransaction(
            @PathVariable String accountId,
            @Valid @RequestBody EventRequest request) {

        if (!accountId.equals(request.accountId())) {
            throw new IllegalArgumentException("Account ID mismatch.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.applyTransaction(request));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getBalance(
            @PathVariable String accountId) {

        return ResponseEntity.ok(accountService.getBalance(accountId));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsResponse> getAccount(
            @PathVariable String accountId) {

        return ResponseEntity.ok(accountService.getAccountDetails(accountId));
    }

}