package io.github.techrbaitha.eventledger.account.exception;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String eventId) {
        super("Duplicate transaction: " + eventId);
    }
}