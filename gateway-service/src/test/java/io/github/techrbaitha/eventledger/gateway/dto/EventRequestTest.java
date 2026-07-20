package io.github.techrbaitha.eventledger.gateway.dto;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    @Test
    void shouldCreateEventRequest() {

        Instant timestamp = Instant.parse("2026-05-15T14:02:11Z");

        EventRequest request = new EventRequest(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                timestamp
        );

        assertEquals("evt-001", request.eventId());
        assertEquals("acct-001", request.accountId());
        assertEquals(TransactionType.CREDIT, request.type());
        assertEquals(new BigDecimal("100.50"), request.amount());
        assertEquals("USD", request.currency());
        assertEquals(timestamp, request.eventTimestamp());
    }
    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        EventRequest first = new EventRequest(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                Instant.parse("2026-05-15T14:02:11Z")
        );

        EventRequest second = new EventRequest(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.50"),
                "USD",
                Instant.parse("2026-05-15T14:02:11Z")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertTrue(first.toString().contains("evt-001"));
    }
}