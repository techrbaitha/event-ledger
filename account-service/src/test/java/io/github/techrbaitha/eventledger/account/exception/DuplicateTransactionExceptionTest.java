package io.github.techrbaitha.eventledger.account.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateTransactionExceptionTest {

    @Test
    void shouldCreateDuplicateTransactionException() {

        DuplicateTransactionException exception =
                new DuplicateTransactionException("evt-001");

        assertEquals(
                "Duplicate transaction: evt-001",
                exception.getMessage()
        );

        assertNull(exception.getCause());
    }
}