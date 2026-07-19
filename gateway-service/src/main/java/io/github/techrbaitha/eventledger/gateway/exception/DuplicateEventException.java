package io.github.techrbaitha.eventledger.gateway.exception;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException(String eventId) {
        super("Event already exists with eventId: " + eventId);
    }
}