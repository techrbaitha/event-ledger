package io.github.techrbaitha.eventledger.gateway.entity;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventEntityTest {

    @Test
    void shouldCreateEventEntityUsingConstructor() {

        Instant timestamp = Instant.parse("2026-05-15T14:02:11Z");

        EventEntity entity = new EventEntity(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("150.50"),
                "USD",
                timestamp
        );

        assertNull(entity.getId());
        assertEquals("evt-001", entity.getEventId());
        assertEquals("acct-001", entity.getAccountId());
        assertEquals(TransactionType.CREDIT, entity.getType());
        assertEquals(new BigDecimal("150.50"), entity.getAmount());
        assertEquals("USD", entity.getCurrency());
        assertEquals(timestamp, entity.getEventTimestamp());
    }

    @Test
    void shouldCreateEntityUsingProtectedConstructor() {

        EventEntity entity = new EventEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getEventId());
        assertNull(entity.getAccountId());
        assertNull(entity.getType());
        assertNull(entity.getAmount());
        assertNull(entity.getCurrency());
        assertNull(entity.getEventTimestamp());
    }
}