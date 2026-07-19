package io.github.techrbaitha.eventledger.gateway.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> validationErrors
) {
}