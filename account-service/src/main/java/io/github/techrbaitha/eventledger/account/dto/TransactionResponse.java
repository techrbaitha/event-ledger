package io.github.techrbaitha.eventledger.account.dto;

public record TransactionResponse(
        String status,
        String message
) {
}