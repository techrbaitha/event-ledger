package io.github.techrbaitha.eventledger.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.techrbaitha.eventledger.account.controller.AccountController;
import io.github.techrbaitha.eventledger.account.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.account.dto.AccountDetailsResponse;
import io.github.techrbaitha.eventledger.account.dto.EventRequest;
import io.github.techrbaitha.eventledger.account.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.account.enums.TransactionType;
import io.github.techrbaitha.eventledger.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void shouldApplyTransaction() throws Exception {

        EventRequest request = new EventRequest(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-05-15T14:02:11Z")
        );

        TransactionResponse response =
                new TransactionResponse(
                        "SUCCESS",
                        "Transaction applied successfully."
                );

        when(accountService.applyTransaction(any()))
                .thenReturn(response);

        mockMvc.perform(post("/accounts/acct-001/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void shouldReturnBadRequestWhenAccountIdMismatch() throws Exception {

        EventRequest request = new EventRequest(
                "evt-001",
                "acct-999",
                TransactionType.CREDIT,
                new BigDecimal("100.00"),
                "USD",
                Instant.now()
        );

        mockMvc.perform(post("/accounts/acct-001/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBalance() throws Exception {

        AccountBalanceResponse response =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("500.00")
                );

        when(accountService.getBalance("acct-001"))
                .thenReturn(response);

        mockMvc.perform(get("/accounts/acct-001/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acct-001"));
    }

    @Test
    void shouldReturnAccountDetails() throws Exception {

        AccountBalanceResponse balance =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("300.00")
                );

        EventRequest transaction =
                new EventRequest(
                        "evt-001",
                        "acct-001",
                        TransactionType.CREDIT,
                        new BigDecimal("300.00"),
                        "USD",
                        Instant.parse("2026-05-15T14:02:11Z")
                );

        AccountDetailsResponse response =
                new AccountDetailsResponse(
                        "acct-001",
                        balance,
                        java.util.List.of(transaction)
                );

        when(accountService.getAccountDetails(eq("acct-001")))
                .thenReturn(response);

        mockMvc.perform(get("/accounts/acct-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acct-001"))
                .andExpect(jsonPath("$.balance.accountId").value("acct-001"))
                .andExpect(jsonPath("$.balance.balance").value(300.00))
                .andExpect(jsonPath("$.transactions[0].eventId").value("evt-001"))
                .andExpect(jsonPath("$.transactions[0].accountId").value("acct-001"));
    }
}