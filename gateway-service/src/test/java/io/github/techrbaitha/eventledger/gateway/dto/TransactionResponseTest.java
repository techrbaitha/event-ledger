package io.github.techrbaitha.eventledger.gateway.dto;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResponseTest {

    @Test
    void shouldCreateTransactionResponse() {

        Instant timestamp = Instant.parse("2026-05-15T14:02:11Z");

        TransactionResponse response = new TransactionResponse(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                timestamp
        );

        assertEquals("evt-001", response.eventId());
        assertEquals("acct-001", response.accountId());
        assertEquals(TransactionType.CREDIT, response.type());
        assertEquals(new BigDecimal("100.50"), response.amount());
        assertEquals("USD", response.currency());
        assertEquals(timestamp, response.eventTimestamp());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        Instant timestamp = Instant.parse("2026-05-15T14:02:11Z");

        TransactionResponse first = new TransactionResponse(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                timestamp
        );

        TransactionResponse second = new TransactionResponse(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                timestamp
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertTrue(first.toString().contains("evt-001"));
        assertTrue(first.toString().contains("acct-001"));
    }
}