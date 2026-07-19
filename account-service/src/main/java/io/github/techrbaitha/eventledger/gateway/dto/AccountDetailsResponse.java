package io.github.techrbaitha.eventledger.gateway.dto;

import java.util.List;

public record AccountDetailsResponse(
        String accountId,
        AccountBalanceResponse balance,
        List<EventRequest> transactions
) {
}