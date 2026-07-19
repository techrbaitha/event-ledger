package io.github.techrbaitha.eventledger.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.techrbaitha.eventledger.gateway.dto.AccountBalanceResponse;
import io.github.techrbaitha.eventledger.gateway.dto.AccountDetailsResponse;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.TransactionResponse;
import io.github.techrbaitha.eventledger.gateway.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

    @MockBean
    private AccountService accountService;

    @Test
    void shouldApplyTransaction() throws Exception {

        EventRequest request = new EventRequest(
                "evt-001",
                "acct-001",
                "CREDIT",
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
                "CREDIT",
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
                        new BigDecimal("500.00"),
                        "USD"
                );

        when(accountService.getBalance("acct-001"))
                .thenReturn(response);

        mockMvc.perform(get("/accounts/acct-001/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acct-001"));
    }

    @Test
    void shouldReturnAccountDetails() throws Exception {

        AccountDetailsResponse response =
                new AccountDetailsResponse(
                        "acct-001",
                        new BigDecimal("500.00"),
                        "USD",
                        List.of()
                );

        when(accountService.getAccountDetails(eq("acct-001")))
                .thenReturn(response);

        mockMvc.perform(get("/accounts/acct-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acct-001"));
    }
}