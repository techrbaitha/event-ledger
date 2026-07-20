package io.github.techrbaitha.eventledger.account.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountBalanceResponseTest {

    @Test
    void shouldCreateAccountBalanceResponse() {

        AccountBalanceResponse response =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("250.50")
                );

        assertEquals("acct-001", response.accountId());
        assertEquals(new BigDecimal("250.50"), response.balance());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        AccountBalanceResponse first =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("250.50")
                );

        AccountBalanceResponse second =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("250.50")
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("acct-001"));
    }
}