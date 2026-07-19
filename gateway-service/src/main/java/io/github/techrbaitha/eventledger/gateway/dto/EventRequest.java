package io.github.techrbaitha.eventledger.gateway.dto;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record EventRequest(

        @NotBlank(message = "eventId is required")
        String eventId,

        @NotBlank(message = "accountId is required")
        String accountId,

        @NotNull(message = "type is required")
        TransactionType type,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        String currency,

        @NotNull(message = "eventTimestamp is required")
        Instant eventTimestamp,

        Map<String, Object> metadata

) {
}