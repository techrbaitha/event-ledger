package io.github.techrbaitha.eventledger.gateway.dto;

public record TransactionResponse(
        String status,
        String message
) {
}