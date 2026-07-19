package io.github.techrbaitha.eventledger.gateway.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor traceInterceptor() {

        return requestTemplate -> {

            String traceId = MDC.get("traceId");

            if (traceId != null) {
                requestTemplate.header("X-Trace-Id", traceId);
            }
        };
    }
}