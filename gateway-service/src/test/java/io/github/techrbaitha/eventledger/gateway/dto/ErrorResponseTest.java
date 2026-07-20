package io.github.techrbaitha.eventledger.gateway.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponse() {

        Instant now = Instant.now();

        ErrorResponse response = new ErrorResponse(
                now,
                400,
                "Bad Request",
                "Validation failed",
                "/events",
                "trace-123"
        );

        assertEquals(now, response.timestamp());
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Validation failed", response.message());
        assertEquals("/events", response.path());
        assertEquals("trace-123", response.traceId());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        Instant now = Instant.parse("2026-05-15T14:02:11Z");

        ErrorResponse first =
                new ErrorResponse(
                        now,
                        400,
                        "Bad Request",
                        "Validation failed",
                        "/events",
                        "trace-123"
                );

        ErrorResponse second =
                new ErrorResponse(
                        now,
                        400,
                        "Bad Request",
                        "Validation failed",
                        "/events",
                        "trace-123"
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertTrue(first.toString().contains("Validation failed"));
    }
}