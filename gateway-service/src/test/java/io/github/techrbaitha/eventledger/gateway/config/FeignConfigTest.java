package io.github.techrbaitha.eventledger.gateway.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.techrbaitha.eventledger.gateway.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class FeignConfigTest {

    private final FeignConfig feignConfig = new FeignConfig();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldCreateRequestInterceptor() {

        RequestInterceptor interceptor = feignConfig.requestInterceptor();

        assertNotNull(interceptor);
    }

    @Test
    void shouldPropagateTraceIdHeader() {

        MDC.put("traceId", "trace-123");

        RequestInterceptor interceptor = feignConfig.requestInterceptor();

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals(
                "trace-123",
                template.headers()
                        .get("X-Trace-Id")
                        .iterator()
                        .next()
        );
    }

    @Test
    void shouldNotAddHeaderWhenTraceIdMissing() {

        RequestInterceptor interceptor = feignConfig.requestInterceptor();

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("X-Trace-Id"));
    }

    @Test
    void shouldCreateFeignLoggerLevel() {

        assertEquals(
                feign.Logger.Level.BASIC,
                feignConfig.feignLoggerLevel()
        );
    }

    @Test
    void shouldNotPropagateBlankTraceId() {

        MDC.put(AppConstants.TRACE_ID, "   ");

        RequestInterceptor interceptor =
                feignConfig.requestInterceptor();

        RequestTemplate template =
                new RequestTemplate();

        interceptor.apply(template);

        assertFalse(
                template.headers()
                        .containsKey(AppConstants.TRACE_ID_HEADER)
        );
    }
}