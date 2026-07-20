package io.github.techrbaitha.eventledger.gateway.exception;

import io.github.techrbaitha.eventledger.gateway.dto.ErrorResponse;
import io.github.techrbaitha.eventledger.gateway.util.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {

        handler = new GlobalExceptionHandler();

        when(request.getRequestURI())
                .thenReturn("/events");

        MDC.put(AppConstants.TRACE_ID, "trace-123");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should handle DuplicateEventException")
    void shouldHandleDuplicateEventException() {

        DuplicateEventException exception =
                new DuplicateEventException("Duplicate event");

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateEvent(exception, request);

        assertEquals(409, response.getStatusCode().value());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Conflict", body.error());
        assertEquals("Duplicate event", body.message());
        assertEquals("/events", body.path());
        assertEquals("trace-123", body.traceId());
    }

    @Test
    @DisplayName("Should handle ServiceUnavailableException")
    void shouldHandleServiceUnavailableException() {

        ServiceUnavailableException exception =
                new ServiceUnavailableException("Account Service unavailable");

        ResponseEntity<ErrorResponse> response =
                handler.handleServiceUnavailable(exception, request);

        assertEquals(503, response.getStatusCode().value());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(503, body.status());
        assertEquals("Service Unavailable", body.error());
        assertEquals("Account Service unavailable", body.message());
        assertEquals("/events", body.path());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void shouldHandleConstraintViolationException() {

        ConstraintViolationException exception =
                new ConstraintViolationException("Invalid input", null);

        ResponseEntity<ErrorResponse> response =
                handler.handleConstraintViolation(exception, request);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("Invalid input", body.message());
    }

    @Test
    @DisplayName("Should handle Generic Exception")
    void shouldHandleGenericException() {

        RuntimeException exception =
                new RuntimeException("Unexpected");

        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(exception, request);

        assertEquals(500, response.getStatusCode().value());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Internal Server Error", body.error());
        assertEquals(AppConstants.INTERNAL_ERROR, body.message());
        assertEquals("/events", body.path());
        assertEquals("trace-123", body.traceId());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void shouldHandleValidationException() {

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError(
                "eventRequest",
                "eventId",
                "Event Id is required"
        );

        FieldError fieldError2 = new FieldError(
                "eventRequest",
                "amount",
                "Amount must be greater than zero"
        );

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationException(exception, request);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());

        assertTrue(body.message().contains("Event Id is required"));
        assertTrue(body.message().contains("Amount must be greater than zero"));

        assertEquals("/events", body.path());
        assertEquals("trace-123", body.traceId());
    }
}