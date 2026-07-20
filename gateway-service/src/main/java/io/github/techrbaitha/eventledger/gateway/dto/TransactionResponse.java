package io.github.techrbaitha.eventledger.gateway.dto;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(

        String eventId,

        String accountId,

        TransactionType type,

        BigDecimal amount,

        String currency,

        Instant eventTimestamp

) {
}