package io.github.techrbaitha.eventledger.account.dto;

import io.github.techrbaitha.eventledger.account.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountDetailsResponseTest {

    @Test
    void shouldCreateAccountDetailsResponse() {

        EventRequest request =
                new EventRequest(
                        "evt-001",
                        "acct-001",
                        TransactionType.CREDIT,
                        new BigDecimal("100"),
                        "USD",
                        Instant.parse("2026-05-15T14:02:11Z")
                );

        AccountBalanceResponse balance =
                new AccountBalanceResponse(
                        "acct-001",
                        new BigDecimal("100")
                );

        AccountDetailsResponse response =
                new AccountDetailsResponse(
                        "acct-001",
                        balance,
                        List.of(request)
                );

        assertEquals("acct-001", response.accountId());
        assertEquals(balance, response.balance());
        assertEquals(1, response.transactions().size());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        AccountDetailsResponse first =
                new AccountDetailsResponse(
                        "acct-001",
                        new AccountBalanceResponse(
                                "acct-001",
                                BigDecimal.ONE
                        ),
                        List.of()
                );

        AccountDetailsResponse second =
                new AccountDetailsResponse(
                        "acct-001",
                        new AccountBalanceResponse(
                                "acct-001",
                                BigDecimal.ONE
                        ),
                        List.of()
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("acct-001"));
    }
}