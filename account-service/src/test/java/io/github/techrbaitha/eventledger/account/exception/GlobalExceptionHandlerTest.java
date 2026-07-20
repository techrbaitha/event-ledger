package io.github.techrbaitha.eventledger.account.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleIllegalArgumentException() {

        IllegalArgumentException exception =
                new IllegalArgumentException("Account ID mismatch.");

        ResponseEntity<String> response =
                handler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account ID mismatch.", response.getBody());
    }

    @Test
    void shouldHandleDuplicateTransactionException() {

        DuplicateTransactionException exception =
                new DuplicateTransactionException("evt-001");

        ResponseEntity<String> response =
                handler.handleDuplicateTransactionException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate transaction: evt-001", response.getBody());
    }
}
