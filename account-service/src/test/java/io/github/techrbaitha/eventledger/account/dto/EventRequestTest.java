package io.github.techrbaitha.eventledger.account.dto;

import io.github.techrbaitha.eventledger.account.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    @Test
    void shouldCreateEventRequest() {

        Instant now = Instant.parse("2026-05-15T14:02:11Z");

        EventRequest request =
                new EventRequest(
                        "evt-001",
                        "acct-001",
                        TransactionType.DEBIT,
                        new BigDecimal("250"),
                        "USD",
                        now
                );

        assertEquals("evt-001", request.eventId());
        assertEquals("acct-001", request.accountId());
        assertEquals(TransactionType.DEBIT, request.type());
        assertEquals(new BigDecimal("250"), request.amount());
        assertEquals("USD", request.currency());
        assertEquals(now, request.eventTimestamp());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        Instant now = Instant.parse("2026-05-15T14:02:11Z");

        EventRequest first =
                new EventRequest(
                        "evt-001",
                        "acct-001",
                        TransactionType.DEBIT,
                        new BigDecimal("250"),
                        "USD",
                        now
                );

        EventRequest second =
                new EventRequest(
                        "evt-001",
                        "acct-001",
                        TransactionType.DEBIT,
                        new BigDecimal("250"),
                        "USD",
                        now
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("evt-001"));
    }
}