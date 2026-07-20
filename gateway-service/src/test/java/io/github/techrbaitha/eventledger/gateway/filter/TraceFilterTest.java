package io.github.techrbaitha.eventledger.gateway.filter;

import io.github.techrbaitha.eventledger.gateway.util.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraceFilterTest {

    private final TraceFilter traceFilter = new TraceFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateTraceIdWhenHeaderMissing() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        traceFilter.doFilterInternal(request, response, filterChain);

        String traceId = response.getHeader(AppConstants.TRACE_ID_HEADER);

        assertNotNull(traceId);
        assertFalse(traceId.isBlank());

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(AppConstants.TRACE_ID));
    }

    @Test
    void shouldReuseIncomingTraceId() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AppConstants.TRACE_ID_HEADER, "trace-123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        traceFilter.doFilterInternal(request, response, filterChain);

        assertEquals(
                "trace-123",
                response.getHeader(AppConstants.TRACE_ID_HEADER)
        );

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(AppConstants.TRACE_ID));
    }

    @Test
    void shouldPopulateMdcBeforeCallingFilterChain() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        doAnswer(invocation -> {

            assertNotNull(MDC.get(AppConstants.TRACE_ID));

            return null;

        }).when(filterChain).doFilter(any(), any());

        traceFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldGenerateTraceIdWhenIncomingHeaderIsBlank()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AppConstants.TRACE_ID_HEADER, "   ");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        traceFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        String traceId =
                response.getHeader(AppConstants.TRACE_ID_HEADER);

        assertNotNull(traceId);
        assertFalse(traceId.isBlank());

        verify(filterChain)
                .doFilter(request, response);
    }
}