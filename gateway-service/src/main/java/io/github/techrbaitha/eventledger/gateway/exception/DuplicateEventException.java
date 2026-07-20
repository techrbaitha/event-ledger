package io.github.techrbaitha.eventledger.gateway.exception;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException(String message) {
        super(message);
    }
}