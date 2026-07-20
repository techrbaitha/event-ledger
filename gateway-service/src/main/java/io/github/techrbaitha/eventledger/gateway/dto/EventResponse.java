package io.github.techrbaitha.eventledger.gateway.dto;

import java.time.Instant;

public record EventResponse(

        String eventId,

        String status,

        String message,

        Instant processedAt

) {
}