package io.github.techrbaitha.eventledger.account.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraceFilterTest {

    private final TraceFilter filter = new TraceFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldStoreTraceIdInMdcAndClearAfterRequest() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "trace-123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            assertEquals("trace-123", MDC.get("traceId"));
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());

        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldNotStoreBlankTraceId() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "   ");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            assertNull(MDC.get("traceId"));
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());

        assertNull(MDC.get("traceId"));
    }
}