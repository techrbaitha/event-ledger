package io.github.techrbaitha.eventledger.gateway.exception;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String eventId) {
        super("Duplicate transaction: " + eventId);
    }
}