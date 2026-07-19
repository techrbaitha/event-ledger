package io.github.techrbaitha.eventledger.gateway.client;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${account-service.url}"
)
public interface AccountServiceClient {

    @PostMapping("/internal/events")
    void processEvent(@RequestBody EventRequest request);

}