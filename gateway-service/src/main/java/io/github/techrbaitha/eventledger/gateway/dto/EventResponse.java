package io.github.techrbaitha.eventledger.gateway.dto;

public record EventResponse(
        String eventId,
        String status,
        String message
) {
}