package io.github.techrbaitha.eventledger.gateway.client;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "account-service",
        url = "${account-service.url}"
)
public interface AccountServiceClient {

    @PostMapping("/accounts/{accountId}/transactions")
    TransactionResponse applyTransaction(
            @PathVariable String accountId,
            @RequestBody EventRequest request);

}