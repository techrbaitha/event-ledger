package io.github.techrbaitha.eventledger.gateway.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventResponseTest {

    @Test
    void shouldCreateEventResponse() {

        Instant processedAt = Instant.now();

        EventResponse response = new EventResponse(
                "evt-001",
                "SUCCESS",
                "Processed successfully",
                processedAt
        );

        assertEquals("evt-001", response.eventId());
        assertEquals("SUCCESS", response.status());
        assertEquals("Processed successfully", response.message());
        assertEquals(processedAt, response.processedAt());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        Instant now = Instant.parse("2026-05-15T14:02:11Z");

        EventResponse first =
                new EventResponse(
                        "evt-001",
                        "SUCCESS",
                        "Processed",
                        now
                );

        EventResponse second =
                new EventResponse(
                        "evt-001",
                        "SUCCESS",
                        "Processed",
                        now
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertTrue(first.toString().contains("SUCCESS"));
    }
}