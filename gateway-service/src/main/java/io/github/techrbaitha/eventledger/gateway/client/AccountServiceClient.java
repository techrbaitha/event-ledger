package io.github.techrbaitha.eventledger.gateway.client;

import io.github.techrbaitha.eventledger.gateway.config.FeignConfig;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {

    @PostMapping("/accounts/{accountId}/transactions")
    ResponseEntity<Void> processTransaction(
            @PathVariable("accountId") String accountId,
            @RequestBody EventRequest request
    );
}