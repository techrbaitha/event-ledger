package io.github.techrbaitha.eventledger.gateway.config;

import feign.Logger;
import feign.RequestInterceptor;
import io.github.techrbaitha.eventledger.gateway.util.AppConstants;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get(AppConstants.TRACE_ID);

            if (traceId != null && !traceId.isBlank()) {
                requestTemplate.header(
                        AppConstants.TRACE_ID_HEADER,
                        traceId
                );
            }
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}